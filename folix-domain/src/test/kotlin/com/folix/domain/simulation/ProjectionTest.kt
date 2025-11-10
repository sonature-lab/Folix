package com.folix.domain.simulation

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class ProjectionTest : DescribeSpec({

    describe("Projection") {

        describe("단순 복리 예측") {
            it("should_project_without_contribution") {
                // 10000 at 10% annual for 12 months
                val points = Projection.project(
                    initialAmount = BigDecimal("10000"),
                    annualReturnRate = BigDecimal("0.10"),
                    months = 12
                )
                points shouldHaveSize 13 // month 0~12
                points[0].value shouldBe BigDecimal("10000.00")
                // 12개월 후 ≈ 10000 * (1 + 0.10/12)^12 ≈ 10471
                points[12].value shouldBeGreaterThan BigDecimal("10400.00")
            }

            it("should_project_with_monthly_contribution") {
                val points = Projection.project(
                    initialAmount = BigDecimal("10000"),
                    monthlyContribution = BigDecimal("500"),
                    annualReturnRate = BigDecimal("0.10"),
                    months = 12
                )
                // 원금 + 12개월 적립(6000) + 이자
                points[12].value shouldBeGreaterThan BigDecimal("16000.00")
            }

            it("should_return_initial_for_month_zero") {
                val points = Projection.project(
                    initialAmount = BigDecimal("5000"),
                    annualReturnRate = BigDecimal("0.08"),
                    months = 6
                )
                points[0].month shouldBe 0
                points[0].value shouldBe BigDecimal("5000.00")
            }
        }

        describe("목표 달성 기간") {
            it("should_calculate_months_to_target") {
                val months = Projection.monthsToTarget(
                    initialAmount = BigDecimal("10000"),
                    monthlyContribution = BigDecimal("1000"),
                    annualReturnRate = BigDecimal("0.10"),
                    targetAmount = BigDecimal("100000")
                )
                months.shouldNotBeNull()
                months shouldBeGreaterThan 0
            }

            it("should_return_null_if_unreachable") {
                // 0% 수익률, 0 적립으로 200000 달성 불가
                val months = Projection.monthsToTarget(
                    initialAmount = BigDecimal("10000"),
                    monthlyContribution = BigDecimal.ZERO,
                    annualReturnRate = BigDecimal.ZERO,
                    targetAmount = BigDecimal("200000"),
                    maxMonths = 100
                )
                months.shouldBeNull()
            }
        }

        describe("유효성 검증") {
            it("should_fail_with_zero_months") {
                shouldThrow<IllegalArgumentException> {
                    Projection.project(BigDecimal("1000"), annualReturnRate = BigDecimal("0.1"), months = 0)
                }
            }

            it("should_fail_when_target_less_than_initial") {
                shouldThrow<IllegalArgumentException> {
                    Projection.monthsToTarget(
                        initialAmount = BigDecimal("10000"),
                        annualReturnRate = BigDecimal("0.1"),
                        targetAmount = BigDecimal("5000")
                    )
                }
            }
        }
    }
})
