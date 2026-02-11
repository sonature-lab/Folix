package com.folix.application.port.out

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 외부 시세 데이터 제공자를 위한 아웃바운드 포트.
 *
 * 자산 유형(주식/크립토)에 따라 적절한 구현체가 주입된다.
 */
interface MarketDataProvider {

    fun fetchCurrentPrice(symbol: String): BigDecimal

    fun fetchHistoricalPrices(
        symbol: String,
        from: LocalDate,
        to: LocalDate
    ): List<Pair<LocalDate, BigDecimal>>

    fun supports(assetType: String): Boolean

    fun sourceName(): String
}
