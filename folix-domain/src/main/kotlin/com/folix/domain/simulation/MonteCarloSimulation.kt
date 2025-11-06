package com.folix.domain.simulation

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

/**
 * 몬테카를로 시뮬레이션 엔진.
 *
 * Geometric Brownian Motion(GBM) 모델을 사용하여 자산 가치를 예측한다.
 * 월별 수익률을 확률적으로 생성하여 다수의 경로를 시뮬레이션하고,
 * 퍼센타일별 결과를 제공한다.
 *
 * GBM 월별 수익률:
 * r_monthly = μ_m - σ²_m/2 + σ_m × Z
 * (Z ~ N(0,1) 표준정규분포)
 */
object MonteCarloSimulation {

    private const val DEFAULT_ITERATIONS = 10_000
    private const val CALC_SCALE = 10

    /**
     * 몬테카를로 시뮬레이션을 실행한다.
     *
     * @param scenario 시나리오 정의
     * @param iterations 시뮬레이션 횟수 (기본: 10,000)
     * @param seed 난수 시드 (재현 가능한 결과용, nullable)
     * @return [SimulationResult] 퍼센타일별 결과 + 월별 시계열
     */
    fun run(
        scenario: Scenario,
        iterations: Int = DEFAULT_ITERATIONS,
        seed: Long? = null
    ): SimulationResult {
        require(iterations > 0) { "Iterations must be positive: $iterations" }

        val random = if (seed != null) Random(seed) else Random.Default

        // Performance note: Monte Carlo 시뮬레이션은 수만 회 반복 연산이므로
        // BigDecimal 대신 Double을 사용한다. 최종 결과만 BigDecimal로 변환하여
        // 통계적 근사의 특성상 정밀도 손실이 결과에 유의미한 영향을 주지 않는다.
        val monthlyMu = scenario.monthlyReturnRate.toDouble()
        val monthlySigma = scenario.monthlyVolatility.toDouble()
        val initial = scenario.initialInvestment.toDouble()
        val monthlyAdd = scenario.monthlyContribution.toDouble()
        val months = scenario.horizonMonths

        // 각 반복의 월별 가치를 저장: [iteration][month]
        val allPaths = Array(iterations) { DoubleArray(months + 1) }

        for (i in 0 until iterations) {
            allPaths[i][0] = initial

            for (m in 1..months) {
                val z = boxMullerNormal(random)
                // GBM: drift + diffusion
                val logReturn = (monthlyMu - monthlySigma * monthlySigma / 2.0) + monthlySigma * z
                val growthFactor = Math.exp(logReturn)

                allPaths[i][m] = allPaths[i][m - 1] * growthFactor + monthlyAdd
            }
        }

        // 최종 가치 추출 및 정렬
        val finalValues = allPaths.map { BigDecimal(it[months]).setScale(CALC_SCALE, RoundingMode.HALF_UP) }
            .sorted()

        val percentiles = SimulationResult.extractPercentiles(finalValues)

        // 월별 시계열 (각 월의 퍼센타일)
        val timeline = (0..months).map { month ->
            val monthValues = allPaths.map { BigDecimal(it[month]).setScale(CALC_SCALE, RoundingMode.HALF_UP) }
                .sorted()
            val mp = SimulationResult.extractPercentiles(monthValues)
            SimulationResult.MonthlyProjection(
                month = month,
                p5 = mp.p5,
                p25 = mp.p25,
                p50 = mp.p50,
                p75 = mp.p75,
                p95 = mp.p95
            )
        }

        return SimulationResult(
            scenarioName = scenario.name,
            iterations = iterations,
            percentiles = percentiles,
            monthlyTimeline = timeline
        )
    }

    /**
     * Box-Muller 변환으로 표준정규분포 난수를 생성한다.
     */
    private fun boxMullerNormal(random: Random): Double {
        val u1 = random.nextDouble()
        val u2 = random.nextDouble()
        return Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2)
    }
}
