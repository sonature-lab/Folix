package com.folix.application.service

import com.folix.application.exception.ExternalApiException
import com.folix.application.exception.MarketDataNotFoundException
import com.folix.application.port.`in`.MarketDataUseCase
import com.folix.application.port.`in`.MarketDataUseCase.ExchangeRateResult
import com.folix.application.port.`in`.MarketDataUseCase.PriceResult
import com.folix.application.port.out.AssetRepository
import com.folix.application.port.out.DailyPriceRepository
import com.folix.application.port.out.ExchangeRateProvider
import com.folix.application.port.out.ExchangeRateRepository
import com.folix.application.port.out.MarketDataProvider
import java.time.LocalDate

/**
 * 시장 데이터 유즈케이스 구현.
 *
 * 조회 전략:
 * 1. 외부 API 호출 (Redis 캐시가 인프라 레이어에서 래핑)
 * 2. 성공 시 DB에도 저장 (write-through)
 * 3. 외부 API 실패 시 DB fallback
 */
class MarketDataService(
    private val marketDataProviders: List<MarketDataProvider>,
    private val exchangeRateProvider: ExchangeRateProvider,
    private val dailyPriceRepository: DailyPriceRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val assetRepository: AssetRepository
) : MarketDataUseCase {

    override fun getPrice(symbol: String): PriceResult {
        val asset = assetRepository.findBySymbol(symbol)
            ?: throw MarketDataNotFoundException(symbol)

        val provider = marketDataProviders.firstOrNull { it.supports(asset.type.name) }
            ?: throw ExternalApiException("NONE", "No provider supports asset type: ${asset.type}")

        return try {
            val price = provider.fetchCurrentPrice(symbol)
            dailyPriceRepository.savePrice(asset.id, LocalDate.now(), price, provider.sourceName())
            PriceResult(symbol, price, asset.currency.code, provider.sourceName(), LocalDate.now())
        } catch (e: Exception) {
            val dbPrice = dailyPriceRepository.findLatestPrice(asset.id)
                ?: throw MarketDataNotFoundException(symbol)
            PriceResult(symbol, dbPrice, asset.currency.code, "DB_FALLBACK", LocalDate.now())
        }
    }

    override fun getExchangeRate(
        baseCurrency: String,
        quoteCurrency: String,
        date: LocalDate?
    ): ExchangeRateResult {
        return try {
            val rate = if (date != null) {
                exchangeRateProvider.fetchRateAt(baseCurrency, quoteCurrency, date)
            } else {
                exchangeRateProvider.fetchLatestRate(baseCurrency, quoteCurrency)
            }
            exchangeRateRepository.save(rate)
            ExchangeRateResult(rate.base.code, rate.quote.code, rate.rate, rate.date, exchangeRateProvider.sourceName())
        } catch (e: Exception) {
            val dbRate = if (date != null) {
                exchangeRateRepository.findRate(baseCurrency, quoteCurrency, date)
            } else {
                exchangeRateRepository.findLatestRate(baseCurrency, quoteCurrency)
            } ?: throw MarketDataNotFoundException("$baseCurrency/$quoteCurrency")
            ExchangeRateResult(dbRate.base.code, dbRate.quote.code, dbRate.rate, dbRate.date, "DB_FALLBACK")
        }
    }
}
