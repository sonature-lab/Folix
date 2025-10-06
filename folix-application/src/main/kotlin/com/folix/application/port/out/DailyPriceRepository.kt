package com.folix.application.port.out

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

/**
 * 일별 시세 데이터를 위한 아웃바운드 포트.
 */
interface DailyPriceRepository {
    fun savePrice(assetId: UUID, date: LocalDate, close: BigDecimal, source: String)
    fun findLatestPrice(assetId: UUID): BigDecimal?
    fun findPriceAt(assetId: UUID, date: LocalDate): BigDecimal?
    fun findPriceRange(assetId: UUID, from: LocalDate, to: LocalDate): List<Pair<LocalDate, BigDecimal>>
}
