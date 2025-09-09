package com.folix.domain.performance

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * 시간가중수익률(TWR) 계산기.
 *
 * TWR은 현금 유출입의 영향을 제거하여 순수 투자 성과를 측정한다.
 * 펀드 매니저 성과 평가의 표준 방법이다.
 *
 * 계산 방법:
 * 1. 현금 흐름이 발생하는 각 기간의 수익률을 구한다.
 * 2. 각 기간 수익률을 기하학적으로 연결한다.
 * TWR = (1 + r1) × (1 + r2) × ... × (1 + rn) - 1
 */
object TimeWeightedReturn {

    private val CALC_SCALE = 12
    private val ONE = BigDecimal.ONE

    /**
     * TWR을 계산한다.
     *
     * @param dailyValues 일별 포트폴리오 가치 시계열 (날짜 순 정렬)
     * @param cashFlows 기간 내 현금 흐름 목록
     * @return TWR (소수, 예: 0.1567 = 15.67%)
     * @throws IllegalArgumentException 데이터가 부족할 때
     */
    fun calculate(
        dailyValues: List<DailyValue>,
        cashFlows: List<CashFlow> = emptyList()
    ): BigDecimal {
        require(dailyValues.size >= 2) { "At least 2 daily values required for TWR calculation" }

        val sortedValues = dailyValues.sorted()
        val cashFlowMap = cashFlows.groupBy { it.date }

        var cumulativeReturn = ONE

        for (i in 1 until sortedValues.size) {
            val prevValue = sortedValues[i - 1].value
            val currValue = sortedValues[i].value
            val currDate = sortedValues[i].date

            // 해당 날짜에 현금 흐름이 있으면 보정
            val dailyCashFlow = cashFlowMap[currDate]
                ?.fold(BigDecimal.ZERO) { acc, cf -> acc + cf.amount }
                ?: BigDecimal.ZERO

            // 기간 수익률 = (현재가치 - 현금흐름) / 이전가치
            val adjustedValue = currValue - dailyCashFlow
            if (prevValue.compareTo(BigDecimal.ZERO) != 0) {
                val periodReturn = adjustedValue.divide(prevValue, CALC_SCALE, RoundingMode.HALF_UP)
                cumulativeReturn = cumulativeReturn.multiply(periodReturn)
            }
        }

        return (cumulativeReturn - ONE).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP)
    }

    /**
     * 연환산 TWR을 계산한다.
     *
     * @param twr 총 TWR (소수)
     * @param days 투자 기간 (일수)
     * @return 연환산 TWR (소수)
     */
    fun annualize(twr: BigDecimal, days: Long): BigDecimal {
        require(days > 0) { "Days must be positive: $days" }
        if (days <= 365) return twr

        val years = BigDecimal(days).divide(BigDecimal(365), CALC_SCALE, RoundingMode.HALF_UP)
        val base = ONE + twr
        // 연환산 = (1 + TWR)^(1/years) - 1
        val exponent = ONE.divide(years, CALC_SCALE, RoundingMode.HALF_UP)
        val annualized = base.toDouble().let { Math.pow(it, exponent.toDouble()) }
        return (BigDecimal(annualized, MathContext.DECIMAL64) - ONE)
            .setScale(DEFAULT_SCALE, RoundingMode.HALF_UP)
    }

    private const val DEFAULT_SCALE = 8
}
