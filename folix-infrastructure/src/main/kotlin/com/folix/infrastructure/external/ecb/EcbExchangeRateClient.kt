package com.folix.infrastructure.external.ecb

import com.folix.application.port.out.ExchangeRateProvider
import com.folix.domain.money.Currency
import com.folix.domain.money.ExchangeRate
import com.folix.infrastructure.external.common.RetryableRestClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * ECB(유럽중앙은행) API를 통한 환율 제공자.
 *
 * ECB는 EUR를 기준으로 환율을 제공하므로, 교차 환율은 계산한다.
 * - EUR/X: 직접 조회
 * - X/EUR: 역수
 * - X/Y: EUR_Y / EUR_X
 */
@Component
class EcbExchangeRateClient(
    @Qualifier("ecbRestClient") private val restClient: RetryableRestClient
) : ExchangeRateProvider {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun fetchLatestRate(baseCurrency: String, quoteCurrency: String): ExchangeRate {
        logger.debug("Fetching latest exchange rate: $baseCurrency/$quoteCurrency")

        if (baseCurrency == quoteCurrency) {
            return ExchangeRate(
                base = Currency.of(baseCurrency),
                quote = Currency.of(quoteCurrency),
                rate = BigDecimal.ONE,
                date = LocalDate.now()
            )
        }

        val xml = restClient.execute("ECB fetchLatestRate($baseCurrency/$quoteCurrency)") {
            get()
                .uri("/eurofxref-daily.xml")
                .retrieve()
                .body(String::class.java)
                ?: throw IllegalStateException("Empty response from ECB")
        }

        val rateMap = EcbXmlParser.parseDailyRates(xml)
        val rate = calculateCrossRate(baseCurrency, quoteCurrency, rateMap)

        return ExchangeRate(
            base = Currency.of(baseCurrency),
            quote = Currency.of(quoteCurrency),
            rate = rate,
            date = LocalDate.now()
        )
    }

    override fun fetchRateAt(
        baseCurrency: String,
        quoteCurrency: String,
        date: LocalDate
    ): ExchangeRate {
        logger.debug("Fetching exchange rate at $date: $baseCurrency/$quoteCurrency")

        if (baseCurrency == quoteCurrency) {
            return ExchangeRate(
                base = Currency.of(baseCurrency),
                quote = Currency.of(quoteCurrency),
                rate = BigDecimal.ONE,
                date = date
            )
        }

        val xml = restClient.execute("ECB fetchRateAt($baseCurrency/$quoteCurrency, $date)") {
            get()
                .uri("/eurofxref-hist-90d.xml")
                .retrieve()
                .body(String::class.java)
                ?: throw IllegalStateException("Empty response from ECB")
        }

        val historicalMap = EcbXmlParser.parseHistoricalRates(xml)

        // 정확한 날짜가 없으면 가장 가까운 이전 날짜 사용
        val targetDate = historicalMap.keys
            .filter { it <= date }
            .maxOrNull()
            ?: throw IllegalStateException("No historical data available for date: $date")

        val rateMap = historicalMap[targetDate]
            ?: throw IllegalStateException("No rate data for date: $targetDate")

        val rate = calculateCrossRate(baseCurrency, quoteCurrency, rateMap)

        return ExchangeRate(
            base = Currency.of(baseCurrency),
            quote = Currency.of(quoteCurrency),
            rate = rate,
            date = targetDate
        )
    }

    override fun sourceName(): String = "ECB"

    /**
     * 교차 환율을 계산한다.
     *
     * ECB는 EUR 기준 환율만 제공하므로:
     * - EUR/X: 직접 조회
     * - X/EUR: 1 / EUR_X
     * - X/Y: EUR_Y / EUR_X
     */
    private fun calculateCrossRate(
        base: String,
        quote: String,
        eurRates: Map<String, BigDecimal>
    ): BigDecimal {
        return when {
            base == "EUR" -> {
                // EUR/X
                eurRates[quote]
                    ?: throw IllegalArgumentException("No rate available for currency: $quote")
            }
            quote == "EUR" -> {
                // X/EUR = 1 / EUR_X
                val eurToBase = eurRates[base]
                    ?: throw IllegalArgumentException("No rate available for currency: $base")
                BigDecimal.ONE.divide(eurToBase, ExchangeRate.RATE_SCALE, RoundingMode.HALF_UP)
            }
            else -> {
                // X/Y = EUR_Y / EUR_X
                val eurToBase = eurRates[base]
                    ?: throw IllegalArgumentException("No rate available for currency: $base")
                val eurToQuote = eurRates[quote]
                    ?: throw IllegalArgumentException("No rate available for currency: $quote")
                eurToQuote.divide(eurToBase, ExchangeRate.RATE_SCALE, RoundingMode.HALF_UP)
            }
        }
    }
}
