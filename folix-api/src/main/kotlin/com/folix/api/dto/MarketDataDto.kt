package com.folix.api.dto

import com.folix.application.port.`in`.MarketDataUseCase.ExchangeRateResult
import com.folix.application.port.`in`.MarketDataUseCase.PriceResult
import java.math.BigDecimal
import java.time.LocalDate

/**
 * 시세 조회 응답 DTO.
 */
data class PriceResponse(
    val symbol: String,
    val price: BigDecimal,
    val currency: String,
    val source: String,
    val date: LocalDate
) {
    companion object {
        fun from(result: PriceResult): PriceResponse = PriceResponse(
            symbol = result.symbol,
            price = result.price,
            currency = result.currency,
            source = result.source,
            date = result.date
        )
    }
}

/**
 * 환율 조회 응답 DTO.
 */
data class ExchangeRateResponse(
    val baseCurrency: String,
    val quoteCurrency: String,
    val rate: BigDecimal,
    val date: LocalDate,
    val source: String
) {
    companion object {
        fun from(result: ExchangeRateResult): ExchangeRateResponse = ExchangeRateResponse(
            baseCurrency = result.baseCurrency,
            quoteCurrency = result.quoteCurrency,
            rate = result.rate,
            date = result.date,
            source = result.source
        )
    }
}
