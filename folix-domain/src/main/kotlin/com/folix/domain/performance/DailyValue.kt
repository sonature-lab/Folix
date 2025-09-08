package com.folix.domain.performance

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 일별 포트폴리오 가치를 나타내는 값 객체.
 *
 * TWR 계산 및 리스크 분석에 사용되는 시계열 데이터 포인트이다.
 *
 * @property date 날짜
 * @property value 포트폴리오 가치
 */
data class DailyValue(
    val date: LocalDate,
    val value: BigDecimal
) : Comparable<DailyValue> {

    init {
        require(value >= BigDecimal.ZERO) { "Portfolio value must be non-negative: $value" }
    }

    override fun compareTo(other: DailyValue): Int = date.compareTo(other.date)
}
