package com.folix.application.service

import com.folix.application.exception.ExternalApiException
import com.folix.application.exception.MarketDataNotFoundException
import com.folix.application.port.out.AssetRepository
import com.folix.application.port.out.DailyPriceRepository
import com.folix.application.port.out.ExchangeRateProvider
import com.folix.application.port.out.ExchangeRateRepository
import com.folix.application.port.out.MarketDataProvider
import com.folix.domain.asset.Asset
import com.folix.domain.money.Currency
import com.folix.domain.money.ExchangeRate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDate

class MarketDataServiceTest : DescribeSpec({

    val stockProvider = mockk<MarketDataProvider>()
    val cryptoProvider = mockk<MarketDataProvider>()
    val exchangeRateProvider = mockk<ExchangeRateProvider>()
    val dailyPriceRepository = mockk<DailyPriceRepository>(relaxed = true)
    val exchangeRateRepository = mockk<ExchangeRateRepository>(relaxed = true)
    val assetRepository = mockk<AssetRepository>()

    val service = MarketDataService(
        marketDataProviders = listOf(stockProvider, cryptoProvider),
        exchangeRateProvider = exchangeRateProvider,
        dailyPriceRepository = dailyPriceRepository,
        exchangeRateRepository = exchangeRateRepository,
        assetRepository = assetRepository
    )

    val apple = Asset.stock("AAPL", "Apple Inc.", exchange = "NASDAQ")
    val bitcoin = Asset.crypto("BTC", "Bitcoin")

    beforeSpec {
        every { stockProvider.supports("STOCK") } returns true
        every { stockProvider.supports("CRYPTO") } returns false
        every { stockProvider.sourceName() } returns "YahooFinance"
        every { cryptoProvider.supports("STOCK") } returns false
        every { cryptoProvider.supports("CRYPTO") } returns true
        every { cryptoProvider.sourceName() } returns "CoinGecko"
        every { exchangeRateProvider.sourceName() } returns "ECB"
    }

    describe("getPrice") {
        it("should_route_to_correct_provider_for_stock") {
            every { assetRepository.findBySymbol("AAPL") } returns apple
            every { stockProvider.fetchCurrentPrice("AAPL") } returns BigDecimal("150.00")

            val result = service.getPrice("AAPL")

            result.symbol shouldBe "AAPL"
            result.price shouldBe BigDecimal("150.00")
            result.source shouldBe "YahooFinance"
            verify { dailyPriceRepository.savePrice(apple.id, any(), BigDecimal("150.00"), "YahooFinance") }
        }

        it("should_route_to_correct_provider_for_crypto") {
            every { assetRepository.findBySymbol("BTC") } returns bitcoin
            every { cryptoProvider.fetchCurrentPrice("BTC") } returns BigDecimal("45000.00")

            val result = service.getPrice("BTC")

            result.symbol shouldBe "BTC"
            result.price shouldBe BigDecimal("45000.00")
            result.source shouldBe "CoinGecko"
        }

        it("should_fallback_to_db_when_provider_fails") {
            every { assetRepository.findBySymbol("AAPL") } returns apple
            every { stockProvider.fetchCurrentPrice("AAPL") } throws RuntimeException("API down")
            every { dailyPriceRepository.findLatestPrice(apple.id) } returns BigDecimal("149.00")

            val result = service.getPrice("AAPL")

            result.symbol shouldBe "AAPL"
            result.price shouldBe BigDecimal("149.00")
            result.source shouldBe "DB_FALLBACK"
        }

        it("should_throw_when_symbol_not_found") {
            every { assetRepository.findBySymbol("UNKNOWN") } returns null

            shouldThrow<MarketDataNotFoundException> {
                service.getPrice("UNKNOWN")
            }
        }

        it("should_throw_when_no_provider_supports_asset_type") {
            val fund = Asset(
                symbol = "VWCE",
                name = "Vanguard FTSE All-World",
                type = com.folix.domain.asset.AssetType.FUND,
                assetClass = com.folix.domain.asset.AssetClass.EQUITY,
                currency = com.folix.domain.money.Currency.USD
            )
            every { assetRepository.findBySymbol("VWCE") } returns fund
            every { stockProvider.supports("FUND") } returns false
            every { cryptoProvider.supports("FUND") } returns false

            shouldThrow<ExternalApiException> {
                service.getPrice("VWCE")
            }
        }

        it("should_throw_when_provider_fails_and_no_db_fallback") {
            every { assetRepository.findBySymbol("AAPL") } returns apple
            every { stockProvider.fetchCurrentPrice("AAPL") } throws RuntimeException("API down")
            every { dailyPriceRepository.findLatestPrice(apple.id) } returns null

            shouldThrow<MarketDataNotFoundException> {
                service.getPrice("AAPL")
            }
        }
    }

    describe("getExchangeRate") {
        val usdKrwRate = ExchangeRate(
            base = Currency.of("USD"),
            quote = Currency.of("KRW"),
            rate = BigDecimal("1350.50"),
            date = LocalDate.of(2026, 2, 11)
        )

        it("should_fetch_latest_exchange_rate") {
            every { exchangeRateProvider.fetchLatestRate("USD", "KRW") } returns usdKrwRate

            val result = service.getExchangeRate("USD", "KRW")

            result.baseCurrency shouldBe "USD"
            result.quoteCurrency shouldBe "KRW"
            result.rate shouldBe BigDecimal("1350.50")
            verify { exchangeRateRepository.save(usdKrwRate) }
        }

        it("should_fetch_exchange_rate_at_date") {
            val date = LocalDate.of(2026, 1, 15)
            val historicalRate = usdKrwRate.copy(date = date)
            every { exchangeRateProvider.fetchRateAt("USD", "KRW", date) } returns historicalRate

            val result = service.getExchangeRate("USD", "KRW", date)

            result.date shouldBe date
            verify { exchangeRateRepository.save(historicalRate) }
        }

        it("should_fallback_to_db_when_provider_fails") {
            every { exchangeRateProvider.fetchLatestRate("USD", "KRW") } throws RuntimeException("API down")
            every { exchangeRateRepository.findLatestRate("USD", "KRW") } returns usdKrwRate

            val result = service.getExchangeRate("USD", "KRW")

            result.rate shouldBe BigDecimal("1350.50")
            result.source shouldBe "DB_FALLBACK"
        }

        it("should_throw_when_no_rate_available") {
            every { exchangeRateProvider.fetchLatestRate("USD", "XXX") } throws RuntimeException("API down")
            every { exchangeRateRepository.findLatestRate("USD", "XXX") } returns null

            shouldThrow<MarketDataNotFoundException> {
                service.getExchangeRate("USD", "XXX")
            }
        }
    }
})
