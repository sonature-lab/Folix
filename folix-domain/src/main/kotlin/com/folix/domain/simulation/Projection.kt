package com.folix.domain.simulation

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 단순 미래 자산 예측기.
 *
 * 확정적(deterministic) 모델로 미래 자산을 예측한다.
 * 몬테카를로처럼 확률적이지 않고, 고정 수익률로 단순 복리 계산한다.
 *
 * 용도: 빠른 목표 달성 시점 추정, 적립식 투자 시뮬레이션
 */
object Projection {

    private const val CALC_SCALE = 10

    /**
     * 단순 복리 예측을 수행한다.
     *
     * FV = PV × (1 + r)^n + PMT × [((1 + r)^n - 1) / r]
     *
     * @param initialAmount 초기 투자금
     * @param monthlyContribution 월별 추가 투자
     * @param annualReturnRate 연 수익률 (소수)
     * @param months 예측 기간 (개월)
     * @return 월별 예측 값 목록
     */
    fun project(
        initialAmount: BigDecimal,
        monthlyContribution: BigDecimal = BigDecimal.ZERO,
        annualReturnRate: BigDecimal,
        months: Int
    ): List<ProjectionPoint> {
        require(months > 0) { "Months must be positive: $months" }

        val monthlyRate = annualReturnRate.divide(BigDecimal(12), CALC_SCALE, RoundingMode.HALF_UP)
        val points = mutableListOf<ProjectionPoint>()
        var currentValue = initialAmount

        points.add(ProjectionPoint(0, currentValue.setScale(2, RoundingMode.HALF_UP)))

        for (m in 1..months) {
            currentValue = currentValue
                .multiply(BigDecimal.ONE + monthlyRate)
                .add(monthlyContribution)
            points.add(ProjectionPoint(m, currentValue.setScale(2, RoundingMode.HALF_UP)))
        }

        return points
    }

    /**
     * 목표 금액 달성에 필요한 개월 수를 계산한다.
     *
     * @return 필요 개월 수 (달성 불가능하면 null)
     */
    fun monthsToTarget(
        initialAmount: BigDecimal,
        monthlyContribution: BigDecimal = BigDecimal.ZERO,
        annualReturnRate: BigDecimal,
        targetAmount: BigDecimal,
        maxMonths: Int = 1200
    ): Int? {
        require(targetAmount > initialAmount) { "Target must be greater than initial amount" }

        val monthlyRate = annualReturnRate.divide(BigDecimal(12), CALC_SCALE, RoundingMode.HALF_UP)
        var currentValue = initialAmount

        for (m in 1..maxMonths) {
            currentValue = currentValue
                .multiply(BigDecimal.ONE + monthlyRate)
                .add(monthlyContribution)
            if (currentValue >= targetAmount) return m
        }

        return null
    }

    /**
     * 예측 데이터 포인트.
     */
    data class ProjectionPoint(
        val month: Int,
        val value: BigDecimal
    )
}
