package com.folix.application.port.out

import com.folix.domain.money.ExchangeRate
import java.time.LocalDate

/**
 * 외부 환율 데이터 제공자를 위한 아웃바운드 포트.
 */
interface ExchangeRateProvider {

    fun fetchLatestRate(baseCurrency: String, quoteCurrency: String): ExchangeRate

    fun fetchRateAt(baseCurrency: String, quoteCurrency: String, date: LocalDate): ExchangeRate

    fun sourceName(): String
}
