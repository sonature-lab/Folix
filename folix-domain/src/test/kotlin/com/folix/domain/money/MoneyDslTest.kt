package com.folix.domain.money

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class MoneyDslTest : DescribeSpec({

    describe("Money DSL") {

        describe("Int 확장") {
            it("should_create_USD_from_int") {
                val money = 10_000.USD
                money.amount shouldBe BigDecimal(10_000)
                money.currency shouldBe Currency.USD
            }

            it("should_create_KRW_from_int") {
                val money = 5_000_000.KRW
                money.amount shouldBe BigDecimal(5_000_000)
                money.currency shouldBe Currency.KRW
            }

            it("should_create_BTC_from_int") {
                val money = 1.BTC
                money.amount shouldBe BigDecimal(1)
                money.currency shouldBe Currency.BTC
            }
        }

        describe("Double 확장") {
            it("should_create_BTC_from_double") {
                val money = 0.5.BTC
                money.amount shouldBe BigDecimal.valueOf(0.5)
                money.currency shouldBe Currency.BTC
            }

            it("should_create_ETH_from_double") {
                val money = 2.5.ETH
                money.currency shouldBe Currency.ETH
            }
        }

        describe("String 확장 (정밀도 보장)") {
            it("should_create_from_string_with_precision") {
                val money = "0.00000001".BTC
                money.amount shouldBe BigDecimal("0.00000001")
                money.currency shouldBe Currency.BTC
            }

            it("should_create_KRW_from_string") {
                val money = "5000000".KRW
                money.amount shouldBe BigDecimal("5000000")
            }
        }

        describe("범용 money 함수") {
            it("should_create_with_custom_currency") {
                val sgd = Currency.of("SGD")
                val money = 100.money(sgd)
                money.amount shouldBe BigDecimal(100)
                money.currency shouldBe sgd
            }
        }

        describe("DSL 산술 조합") {
            it("should_support_arithmetic_with_dsl") {
                val total = 100.USD + 50.USD
                total.amount shouldBe BigDecimal("150")
                total.currency shouldBe Currency.USD
            }

            it("should_support_multiply_with_dsl") {
                val result = 10.USD * 3
                result.amount shouldBe BigDecimal("30")
            }
        }
    }
})
