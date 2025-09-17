package com.folix.domain.performance

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class RiskMetricsTest : DescribeSpec({

    // 10일간의 포트폴리오 가치 시계열
    val dailyValues = listOf(
        DailyValue(LocalDate.of(2025, 1, 1), BigDecimal("1000")),
        DailyValue(LocalDate.of(2025, 1, 2), BigDecimal("1020")),
        DailyValue(LocalDate.of(2025, 1, 3), BigDecimal("1015")),
        DailyValue(LocalDate.of(2025, 1, 4), BigDecimal("1040")),
        DailyValue(LocalDate.of(2025, 1, 5), BigDecimal("1030")),
        DailyValue(LocalDate.of(2025, 1, 6), BigDecimal("1050")),
        DailyValue(LocalDate.of(2025, 1, 7), BigDecimal("1045")),
        DailyValue(LocalDate.of(2025, 1, 8), BigDecimal("1060")),
        DailyValue(LocalDate.of(2025, 1, 9), BigDecimal("1055")),
        DailyValue(LocalDate.of(2025, 1, 10), BigDecimal("1070"))
    )

    describe("RiskMetrics") {

        describe("일별 수익률") {
            it("should_calculate_daily_returns") {
                val returns = RiskMetrics.dailyReturns(dailyValues)
                returns.size shouldBe 9 // n-1 returns

                // 첫 번째 수익률: (1020-1000)/1000 = 0.02
                returns[0].setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("0.02")
            }

            it("should_fail_with_less_than_2_values") {
                shouldThrow<IllegalArgumentException> {
                    RiskMetrics.dailyReturns(listOf(DailyValue(LocalDate.now(), BigDecimal("1000"))))
                }
            }
        }

        describe("연환산 변동성") {
            it("should_calculate_annualized_volatility") {
                val vol = RiskMetrics.annualizedVolatility(dailyValues)
                // 변동성은 양수여야 함
                vol shouldBeGreaterThan BigDecimal.ZERO
                // 이 데이터의 변동성은 적당한 범위 내
                vol shouldBeLessThan BigDecimal("1.0")
            }
        }

        describe("샤프비율") {
            it("should_calculate_sharpe_ratio") {
                val sharpe = RiskMetrics.sharpeRatio(
                    annualizedReturn = BigDecimal("0.15"),
                    annualizedVolatility = BigDecimal("0.20"),
                    riskFreeRate = BigDecimal("0.04")
                )
                // (0.15 - 0.04) / 0.20 = 0.55
                sharpe.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("0.55")
            }

            it("should_return_zero_when_volatility_is_zero") {
                val sharpe = RiskMetrics.sharpeRatio(
                    annualizedReturn = BigDecimal("0.10"),
                    annualizedVolatility = BigDecimal.ZERO
                )
                sharpe shouldBe BigDecimal.ZERO
            }

            it("should_be_negative_when_return_below_risk_free") {
                val sharpe = RiskMetrics.sharpeRatio(
                    annualizedReturn = BigDecimal("0.02"),
                    annualizedVolatility = BigDecimal("0.20"),
                    riskFreeRate = BigDecimal("0.04")
                )
                sharpe shouldBeLessThan BigDecimal.ZERO
            }
        }

        describe("최대낙폭 (MDD)") {
            it("should_calculate_mdd") {
                val mdd = RiskMetrics.maxDrawdown(dailyValues)
                // MDD는 음수 (하락폭)
                mdd shouldBeLessThan BigDecimal.ZERO
            }

            it("should_calculate_mdd_for_significant_drawdown") {
                val values = listOf(
                    DailyValue(LocalDate.of(2025, 1, 1), BigDecimal("100")),
                    DailyValue(LocalDate.of(2025, 1, 2), BigDecimal("120")),
                    DailyValue(LocalDate.of(2025, 1, 3), BigDecimal("90")),  // 고점 120에서 90으로 = -25%
                    DailyValue(LocalDate.of(2025, 1, 4), BigDecimal("110"))
                )
                val mdd = RiskMetrics.maxDrawdown(values)
                mdd.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("-0.25")
            }

            it("should_return_zero_for_monotonic_increase") {
                val values = listOf(
                    DailyValue(LocalDate.of(2025, 1, 1), BigDecimal("100")),
                    DailyValue(LocalDate.of(2025, 1, 2), BigDecimal("110")),
                    DailyValue(LocalDate.of(2025, 1, 3), BigDecimal("120"))
                )
                val mdd = RiskMetrics.maxDrawdown(values)
                mdd shouldBe BigDecimal("0.00000000")
            }
        }

        describe("종합 계산") {
            it("should_calculate_all_metrics") {
                val result = RiskMetrics.calculate(
                    dailyValues = dailyValues,
                    annualizedReturn = BigDecimal("0.15")
                )
                result.annualizedVolatility shouldBeGreaterThan BigDecimal.ZERO
                result.maxDrawdown shouldBeLessThan BigDecimal.ZERO
            }
        }
    }
})
