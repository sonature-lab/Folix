package com.folix.application.port.out

import com.folix.domain.money.ExchangeRate
import java.time.LocalDate

/**
 * 환율 데이터를 위한 아웃바운드 포트.
 */
interface ExchangeRateRepository {
    fun save(exchangeRate: ExchangeRate): ExchangeRate
    fun findRate(baseCurrency: String, quoteCurrency: String, date: LocalDate): ExchangeRate?
    fun findLatestRate(baseCurrency: String, quoteCurrency: String): ExchangeRate?
}
