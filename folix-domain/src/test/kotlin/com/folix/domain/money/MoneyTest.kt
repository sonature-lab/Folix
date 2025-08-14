package com.folix.domain.money

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class MoneyTest : DescribeSpec({

    describe("Money") {

        describe("생성") {
            it("should_create_with_bigdecimal_and_currency") {
                val money = Money(BigDecimal("100.50"), Currency.USD)
                money.amount shouldBe BigDecimal("100.50")
                money.currency shouldBe Currency.USD
            }

            it("should_create_zero") {
                val zero = Money.zero(Currency.KRW)
                zero.amount shouldBe BigDecimal.ZERO
                zero.isZero shouldBe true
            }

            it("should_create_from_string") {
                val money = Money.of("12345.67890000", Currency.BTC)
                money.amount shouldBe BigDecimal("12345.67890000")
            }
        }

        describe("산술 연산") {
            val a = Money(BigDecimal("100"), Currency.USD)
            val b = Money(BigDecimal("50"), Currency.USD)

            it("should_add_same_currency") {
                val result = a + b
                result.amount shouldBe BigDecimal("150")
                result.currency shouldBe Currency.USD
            }

            it("should_subtract_same_currency") {
                val result = a - b
                result.amount shouldBe BigDecimal("50")
            }

            it("should_multiply_by_scalar") {
                val result = a * BigDecimal("3")
                result.amount shouldBe BigDecimal("300")
            }

            it("should_multiply_by_int") {
                val result = a * 3
                result.amount shouldBe BigDecimal("300")
            }

            it("should_divide_with_scale") {
                val money = Money(BigDecimal("100"), Currency.USD)
                val result = money.divide(BigDecimal("3"))
                result.amount.toPlainString() shouldBe "33.33333333"
            }

            it("should_negate") {
                val result = -a
                result.amount shouldBe BigDecimal("-100")
            }

            it("should_return_absolute") {
                val negative = Money(BigDecimal("-50"), Currency.USD)
                negative.abs().amount shouldBe BigDecimal("50")
            }
        }

        describe("통화 불일치 검증") {
            val usd = Money(BigDecimal("100"), Currency.USD)
            val eur = Money(BigDecimal("100"), Currency.EUR)

            it("should_fail_when_adding_different_currencies") {
                shouldThrow<IllegalArgumentException> {
                    usd + eur
                }.message shouldBe "Currency mismatch: cannot operate on USD and EUR"
            }

            it("should_fail_when_subtracting_different_currencies") {
                shouldThrow<IllegalArgumentException> {
                    usd - eur
                }
            }

            it("should_fail_when_comparing_different_currencies") {
                shouldThrow<IllegalArgumentException> {
                    usd.compareTo(eur)
                }
            }
        }

        describe("나눗셈 엣지케이스") {
            it("should_fail_when_dividing_by_zero") {
                shouldThrow<IllegalArgumentException> {
                    Money(BigDecimal("100"), Currency.USD).divide(BigDecimal.ZERO)
                }.message shouldBe "Cannot divide by zero"
            }
        }

        describe("비교") {
            it("should_compare_by_amount") {
                val big = Money(BigDecimal("200"), Currency.USD)
                val small = Money(BigDecimal("100"), Currency.USD)
                big shouldBeGreaterThan small
                small shouldBeLessThan big
            }
        }

        describe("상태 확인") {
            it("should_detect_positive") {
                Money(BigDecimal("1"), Currency.USD).isPositive shouldBe true
                Money(BigDecimal("-1"), Currency.USD).isPositive shouldBe false
            }

            it("should_detect_negative") {
                Money(BigDecimal("-1"), Currency.USD).isNegative shouldBe true
                Money(BigDecimal("1"), Currency.USD).isNegative shouldBe false
            }

            it("should_detect_zero") {
                Money.zero(Currency.USD).isZero shouldBe true
                Money(BigDecimal("1"), Currency.USD).isZero shouldBe false
            }
        }

        describe("toString") {
            it("should_format_amount_and_currency") {
                Money(BigDecimal("1234.56"), Currency.USD).toString() shouldBe "1234.56 USD"
            }
        }
    }
})
