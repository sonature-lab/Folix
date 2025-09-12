package com.folix.domain.performance

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * 리스크 지표 계산기.
 *
 * 포트폴리오의 위험을 정량적으로 측정하는 지표들을 계산한다.
 * - 연환산 변동성 (Annualized Volatility)
 * - 샤프비율 (Sharpe Ratio)
 * - 최대낙폭 (Maximum Drawdown, MDD)
 */
object RiskMetrics {

    private const val CALC_SCALE = 12
    private const val DEFAULT_SCALE = 8
    private val TRADING_DAYS = BigDecimal("252")
    private val HUNDRED = BigDecimal("100")

    /**
     * 일별 수익률 시리즈를 계산한다.
     *
     * @param dailyValues 일별 포트폴리오 가치 (날짜 순)
     * @return 일별 수익률 리스트 (소수)
     */
    fun dailyReturns(dailyValues: List<DailyValue>): List<BigDecimal> {
        require(dailyValues.size >= 2) { "At least 2 values required" }

        val sorted = dailyValues.sorted()
        return sorted.zipWithNext().map { (prev, curr) ->
            if (prev.value.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO
            else (curr.value - prev.value).divide(prev.value, CALC_SCALE, RoundingMode.HALF_UP)
        }
    }

    /**
     * 연환산 변동성을 계산한다.
     *
     * 연환산 변동성 = 일별 수익률의 표준편차 × √252
     *
     * @param dailyValues 일별 포트폴리오 가치
     * @return 연환산 변동성 (소수, 예: 0.25 = 25%)
     */
    fun annualizedVolatility(dailyValues: List<DailyValue>): BigDecimal {
        val returns = dailyReturns(dailyValues)
        if (returns.isEmpty()) return BigDecimal.ZERO

        val stdDev = standardDeviation(returns)
        val annualized = stdDev.multiply(
            BigDecimal(Math.sqrt(TRADING_DAYS.toDouble()), MathContext.DECIMAL64)
        )
        return annualized.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP)
    }

    /**
     * 샤프비율을 계산한다.
     *
     * Sharpe Ratio = (포트폴리오 수익률 - 무위험수익률) / 변동성
     *
     * @param annualizedReturn 연환산 수익률 (소수)
     * @param annualizedVolatility 연환산 변동성 (소수)
     * @param riskFreeRate 무위험수익률 (소수, 기본: 0.04 = 4%)
     * @return 샤프비율
     */
    fun sharpeRatio(
        annualizedReturn: BigDecimal,
        annualizedVolatility: BigDecimal,
        riskFreeRate: BigDecimal = BigDecimal("0.04")
    ): BigDecimal {
        if (annualizedVolatility.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO

        return (annualizedReturn - riskFreeRate)
            .divide(annualizedVolatility, DEFAULT_SCALE, RoundingMode.HALF_UP)
    }

    /**
     * 최대낙폭(MDD)을 계산한다.
     *
     * MDD = 고점 대비 최대 하락폭 (%)
     *
     * @param dailyValues 일별 포트폴리오 가치
     * @return MDD (소수, 예: -0.15 = -15%, 음수로 반환)
     */
    fun maxDrawdown(dailyValues: List<DailyValue>): BigDecimal {
        require(dailyValues.isNotEmpty()) { "Daily values must not be empty" }

        val sorted = dailyValues.sorted()
        var peak = BigDecimal.ZERO
        var maxDd = BigDecimal.ZERO

        for (dv in sorted) {
            if (dv.value > peak) {
                peak = dv.value
            }
            if (peak.compareTo(BigDecimal.ZERO) != 0) {
                val drawdown = (dv.value - peak).divide(peak, CALC_SCALE, RoundingMode.HALF_UP)
                if (drawdown < maxDd) {
                    maxDd = drawdown
                }
            }
        }

        return maxDd.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP)
    }

    /**
     * 종합 리스크 지표를 한 번에 계산한다.
     */
    fun calculate(
        dailyValues: List<DailyValue>,
        annualizedReturn: BigDecimal,
        riskFreeRate: BigDecimal = BigDecimal("0.04")
    ): RiskMetricsResult {
        val volatility = annualizedVolatility(dailyValues)
        val sharpe = sharpeRatio(annualizedReturn, volatility, riskFreeRate)
        val mdd = maxDrawdown(dailyValues)

        return RiskMetricsResult(
            annualizedVolatility = volatility,
            sharpeRatio = sharpe,
            maxDrawdown = mdd
        )
    }

    /**
     * 표준편차를 계산한다.
     */
    private fun standardDeviation(values: List<BigDecimal>): BigDecimal {
        if (values.size < 2) return BigDecimal.ZERO

        val n = BigDecimal(values.size)
        val mean = values.fold(BigDecimal.ZERO) { acc, v -> acc + v }
            .divide(n, CALC_SCALE, RoundingMode.HALF_UP)

        val variance = values.fold(BigDecimal.ZERO) { acc, v ->
            val diff = v - mean
            acc + diff.multiply(diff)
        }.divide(n - BigDecimal.ONE, CALC_SCALE, RoundingMode.HALF_UP) // sample variance (n-1)

        return BigDecimal(Math.sqrt(variance.toDouble()), MathContext.DECIMAL64)
    }
}

/**
 * 리스크 지표 결과를 담는 데이터 클래스.
 */
data class RiskMetricsResult(
    val annualizedVolatility: BigDecimal,
    val sharpeRatio: BigDecimal,
    val maxDrawdown: BigDecimal
)
