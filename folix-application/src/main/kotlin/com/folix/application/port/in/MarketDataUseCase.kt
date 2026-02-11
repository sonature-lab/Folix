package com.folix.application.port.`in`

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 시장 데이터 조회 인바운드 포트.
 */
interface MarketDataUseCase {

    data class PriceResult(
        val symbol: String,
        val price: BigDecimal,
        val currency: String,
        val source: String,
        val date: LocalDate
    )

    data class ExchangeRateResult(
        val baseCurrency: String,
        val quoteCurrency: String,
        val rate: BigDecimal,
        val date: LocalDate,
        val source: String
    )

    fun getPrice(symbol: String): PriceResult

    fun getExchangeRate(
        baseCurrency: String,
        quoteCurrency: String,
        date: LocalDate? = null
    ): ExchangeRateResult
}
