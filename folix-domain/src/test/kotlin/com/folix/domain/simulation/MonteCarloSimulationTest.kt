package com.folix.domain.simulation

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class MonteCarloSimulationTest : DescribeSpec({

    val scenario = Scenario(
        name = "S&P 500 장기 투자",
        initialInvestment = BigDecimal("10000"),
        monthlyContribution = BigDecimal("500"),
        annualReturnRate = BigDecimal("0.10"),
        annualVolatility = BigDecimal("0.20"),
        horizonMonths = 120 // 10년
    )

    describe("MonteCarloSimulation") {

        describe("기본 시뮬레이션") {
            it("should_run_with_default_iterations") {
                val result = MonteCarloSimulation.run(scenario, iterations = 1000, seed = 42L)
                result.iterations shouldBe 1000
                result.scenarioName shouldBe "S&P 500 장기 투자"
            }

            it("should_produce_percentile_ordering") {
                val result = MonteCarloSimulation.run(scenario, iterations = 1000, seed = 42L)
                result.percentiles.p5 shouldBeLessThan result.percentiles.p25
                result.percentiles.p25 shouldBeLessThan result.percentiles.p50
                result.percentiles.p50 shouldBeLessThan result.percentiles.p75
                result.percentiles.p75 shouldBeLessThan result.percentiles.p95
            }

            it("should_produce_values_greater_than_initial") {
                // 10년 투자 + 월 500 적립: p50은 최소한 원금 합산보다 클 가능성 높음
                val result = MonteCarloSimulation.run(scenario, iterations = 1000, seed = 42L)
                val totalInvested = BigDecimal("10000").add(BigDecimal("500").multiply(BigDecimal("120")))
                // p50 (중앙값)이 총 투자금보다 클 것으로 기대
                result.percentiles.p50 shouldBeGreaterThan totalInvested
            }
        }

        describe("월별 타임라인") {
            it("should_produce_monthly_timeline") {
                val result = MonteCarloSimulation.run(scenario, iterations = 100, seed = 42L)
                result.monthlyTimeline shouldHaveSize 121 // month 0 ~ 120
                result.monthlyTimeline.first().month shouldBe 0
                result.monthlyTimeline.last().month shouldBe 120
            }

            it("should_start_at_initial_investment") {
                val result = MonteCarloSimulation.run(scenario, iterations = 100, seed = 42L)
                result.monthlyTimeline[0].p50 shouldBe BigDecimal("10000.00")
            }
        }

        describe("재현 가능성") {
            it("should_produce_same_result_with_same_seed") {
                val result1 = MonteCarloSimulation.run(scenario, iterations = 100, seed = 12345L)
                val result2 = MonteCarloSimulation.run(scenario, iterations = 100, seed = 12345L)
                result1.percentiles shouldBe result2.percentiles
            }
        }

        describe("시나리오 변형") {
            it("should_handle_no_monthly_contribution") {
                val simpleScenario = scenario.copy(monthlyContribution = BigDecimal.ZERO)
                val result = MonteCarloSimulation.run(simpleScenario, iterations = 100, seed = 42L)
                result.percentiles.p50 shouldBeGreaterThan BigDecimal.ZERO
            }

            it("should_handle_short_horizon") {
                val shortScenario = scenario.copy(horizonMonths = 1)
                val result = MonteCarloSimulation.run(shortScenario, iterations = 100, seed = 42L)
                result.monthlyTimeline shouldHaveSize 2 // month 0, 1
            }
        }
    }
})
