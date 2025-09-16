package com.folix.domain.performance

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class TimeWeightedReturnTest : DescribeSpec({

    describe("TimeWeightedReturn") {

        describe("기본 TWR 계산") {
            it("should_calculate_simple_return") {
                // 1000 -> 1100 = 10% return
                val values = listOf(
                    DailyValue(LocalDate.of(2025, 1, 1), BigDecimal("1000")),
                    DailyValue(LocalDate.of(2025, 12, 31), BigDecimal("1100"))
                )
                val twr = TimeWeightedReturn.calculate(values)
                twr.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("0.10")
            }

            it("should_calculate_negative_return") {
                // 1000 -> 900 = -10% return
                val values = listOf(
                    DailyValue(LocalDate.of(2025, 1, 1), BigDecimal("1000")),
                    DailyValue(LocalDate.of(2025, 12, 31), BigDecimal("900"))
                )
                val twr = TimeWeightedReturn.calculate(values)
                twr.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("-0.10")
            }

            it("should_calculate_multi_period_return") {
                // 1000 -> 1100 -> 1210: (1.1) * (1.1) - 1 = 0.21
                val values = listOf(
                    DailyValue(LocalDate.of(2025, 1, 1), BigDecimal("1000")),
                    DailyValue(LocalDate.of(2025, 6, 1), BigDecimal("1100")),
                    DailyValue(LocalDate.of(2025, 12, 31), BigDecimal("1210"))
                )
                val twr = TimeWeightedReturn.calculate(values)
                twr.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("0.21")
            }
        }

        describe("현금 흐름 보정") {
            it("should_adjust_for_cash_inflow") {
                // 1000 -> (1100 + 500 추가투자) -> 1760
                // Period 1: 1100/1000 = 1.1
                // Period 2: (1760 - 500) / 1100 = 1260/1100 = 1.1454...
                // TWR = 1.1 * 1.1454 - 1 ≈ 0.26
                val values = listOf(
                    DailyValue(LocalDate.of(2025, 1, 1), BigDecimal("1000")),
                    DailyValue(LocalDate.of(2025, 6, 1), BigDecimal("1100")),
                    DailyValue(LocalDate.of(2025, 12, 31), BigDecimal("1760"))
                )
                val cashFlows = listOf(
                    CashFlow(LocalDate.of(2025, 12, 31), BigDecimal("500"))
                )
                val twr = TimeWeightedReturn.calculate(values, cashFlows)
                twr shouldBeGreaterThan BigDecimal("0.25")
                twr shouldBeLessThan BigDecimal("0.27")
            }
        }

        describe("연환산") {
            it("should_annualize_2_year_return") {
                // 21% over 2 years → annual ≈ 10%
                val annualized = TimeWeightedReturn.annualize(BigDecimal("0.21"), 730)
                annualized.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("0.10")
            }

            it("should_return_same_for_less_than_one_year") {
                val twr = BigDecimal("0.15")
                TimeWeightedReturn.annualize(twr, 200) shouldBe twr
            }
        }

        describe("엣지케이스") {
            it("should_fail_with_less_than_2_values") {
                shouldThrow<IllegalArgumentException> {
                    TimeWeightedReturn.calculate(listOf(DailyValue(LocalDate.now(), BigDecimal("1000"))))
                }
            }

            it("should_fail_with_zero_days_annualize") {
                shouldThrow<IllegalArgumentException> {
                    TimeWeightedReturn.annualize(BigDecimal("0.1"), 0)
                }
            }
        }
    }
})
