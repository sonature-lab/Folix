package com.folix.domain.simulation

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * 순자산 시계열을 나타내는 값 객체.
 *
 * 일별/주별/월별 순자산 변화를 추적하고 분석한다.
 *
 * @property entries 시계열 엔트리 목록
 */
data class NetWorthTimeline(
    val entries: List<Entry>
) {

    init {
        require(entries.isNotEmpty()) { "Timeline entries must not be empty" }
    }

    /**
     * 시계열 엔트리.
     */
    data class Entry(
        val date: LocalDate,
        val value: BigDecimal
    ) : Comparable<Entry> {
        override fun compareTo(other: Entry): Int = date.compareTo(other.date)
    }

    /** 시작일 */
    val startDate: LocalDate get() = entries.min().date

    /** 종료일 */
    val endDate: LocalDate get() = entries.max().date

    /** 시작 순자산 */
    val startValue: BigDecimal get() = entries.min().value

    /** 최종 순자산 */
    val endValue: BigDecimal get() = entries.max().value

    /** 최고 순자산 */
    val peakValue: BigDecimal get() = entries.maxOf { it.value }

    /** 최저 순자산 */
    val troughValue: BigDecimal get() = entries.minOf { it.value }

    /**
     * 총 변화율을 계산한다 (%).
     */
    fun totalChangeRate(): BigDecimal {
        if (startValue.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO
        return (endValue - startValue)
            .divide(startValue, 8, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    /**
     * 특정 기간의 서브 타임라인을 반환한다.
     */
    fun subTimeline(from: LocalDate, to: LocalDate): NetWorthTimeline {
        val filtered = entries.filter { it.date in from..to }
        require(filtered.isNotEmpty()) { "No entries found between $from and $to" }
        return NetWorthTimeline(filtered)
    }

    companion object {
        /**
         * 날짜-금액 쌍으로 타임라인을 생성한다.
         */
        fun of(vararg pairs: Pair<LocalDate, BigDecimal>): NetWorthTimeline {
            return NetWorthTimeline(pairs.map { Entry(it.first, it.second) })
        }
    }
}
