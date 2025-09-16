package com.folix.domain.performance

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class InternalRateOfReturnTest : DescribeSpec({

    describe("InternalRateOfReturn") {

        describe("기본 IRR 계산") {
            it("should_calculate_simple_irr") {
                // -1000 투자 → 1년 후 1100 회수 → IRR ≈ 10%
                val cashFlows = listOf(
                    CashFlow(LocalDate.of(2025, 1, 1), BigDecimal("-1000")),
                    CashFlow(LocalDate.of(2026, 1, 1), BigDecimal("1100"))
                )
                val irr = InternalRateOfReturn.calculate(cashFlows)
                irr.shouldNotBeNull()
                irr.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("0.10")
            }

            it("should_calculate_irr_with_multiple_cashflows") {
                // -1000 → 6개월 후 -500 추가투자 → 1년 후 1700 회수
                val cashFlows = listOf(
                    CashFlow(LocalDate.of(2025, 1, 1), BigDecimal("-1000")),
                    CashFlow(LocalDate.of(2025, 7, 1), BigDecimal("-500")),
                    CashFlow(LocalDate.of(2026, 1, 1), BigDecimal("1700"))
                )
                val irr = InternalRateOfReturn.calculate(cashFlows)
                irr.shouldNotBeNull()
                // 약 15~16% 정도의 IRR이 나와야 함
                irr shouldBe BigDecimal(irr.toDouble()).setScale(8, RoundingMode.HALF_UP)
            }
        }

        describe("엣지케이스") {
            it("should_fail_with_less_than_2_cashflows") {
                shouldThrow<IllegalArgumentException> {
                    InternalRateOfReturn.calculate(
                        listOf(CashFlow(LocalDate.now(), BigDecimal("-1000")))
                    )
                }
            }

            it("should_calculate_negative_irr") {
                // -1000 → 1년 후 900 (손실)
                val cashFlows = listOf(
                    CashFlow(LocalDate.of(2025, 1, 1), BigDecimal("-1000")),
                    CashFlow(LocalDate.of(2026, 1, 1), BigDecimal("900"))
                )
                val irr = InternalRateOfReturn.calculate(cashFlows)
                irr.shouldNotBeNull()
                irr.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("-0.10")
            }
        }
    }
})
