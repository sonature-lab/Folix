package com.folix.domain.simulation

import java.math.BigDecimal

/**
 * "만약 ~했더라면" 시나리오를 나타내는 값 객체.
 *
 * 과거 또는 가상의 투자 시나리오를 정의하여 결과를 시뮬레이션한다.
 *
 * @property name 시나리오 이름 (예: "1년 전 BTC 매수")
 * @property initialInvestment 초기 투자금
 * @property monthlyContribution 월별 추가 투자 (기본: 0)
 * @property annualReturnRate 연 기대 수익률 (소수, 예: 0.10 = 10%)
 * @property annualVolatility 연 변동성 (소수, 예: 0.20 = 20%)
 * @property horizonMonths 투자 기간 (개월)
 */
data class Scenario(
    val name: String,
    val initialInvestment: BigDecimal,
    val monthlyContribution: BigDecimal = BigDecimal.ZERO,
    val annualReturnRate: BigDecimal,
    val annualVolatility: BigDecimal,
    val horizonMonths: Int
) {

    init {
        require(name.isNotBlank()) { "Scenario name must not be blank" }
        require(initialInvestment >= BigDecimal.ZERO) { "Initial investment must be non-negative" }
        require(monthlyContribution >= BigDecimal.ZERO) { "Monthly contribution must be non-negative" }
        require(horizonMonths > 0) { "Horizon months must be positive: $horizonMonths" }
    }

    /** 월별 기대 수익률 */
    val monthlyReturnRate: BigDecimal
        get() = annualReturnRate.divide(BigDecimal(12), 10, java.math.RoundingMode.HALF_UP)

    /** 월별 변동성 */
    val monthlyVolatility: BigDecimal
        get() = annualVolatility.divide(
            BigDecimal(Math.sqrt(12.0)),
            10,
            java.math.RoundingMode.HALF_UP
        )
}
