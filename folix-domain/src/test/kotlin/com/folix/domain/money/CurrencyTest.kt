package com.folix.domain.money

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CurrencyTest : DescribeSpec({

    describe("Currency") {

        describe("사전 정의 통화") {
            it("should_return_predefined_instances") {
                Currency.USD.code shouldBe "USD"
                Currency.KRW.code shouldBe "KRW"
                Currency.BTC.code shouldBe "BTC"
                Currency.ETH.code shouldBe "ETH"
            }
        }

        describe("of 팩토리") {
            it("should_return_cached_instance_when_predefined") {
                val usd = Currency.of("USD")
                usd shouldBe Currency.USD
            }

            it("should_normalize_to_uppercase") {
                val eur = Currency.of("eur")
                eur shouldBe Currency.EUR
            }

            it("should_create_custom_currency") {
                val custom = Currency.of("XRP")
                custom.code shouldBe "XRP"
            }

            it("should_cache_custom_currency") {
                val first = Currency.of("SOL")
                val second = Currency.of("SOL")
                first shouldBe second
            }
        }

        describe("유효성 검증") {
            it("should_fail_when_code_is_blank") {
                shouldThrow<IllegalArgumentException> {
                    Currency.of("")
                }
            }

            it("should_fail_when_code_is_too_long") {
                shouldThrow<IllegalArgumentException> {
                    Currency.of("TOOLONG")
                }
            }
        }

        describe("동등성") {
            it("should_be_equal_when_same_code") {
                Currency.of("USD") shouldBe Currency.of("USD")
            }

            it("should_not_be_equal_when_different_code") {
                Currency.USD shouldNotBe Currency.EUR
            }
        }

        describe("toString") {
            it("should_return_code") {
                Currency.USD.toString() shouldBe "USD"
            }
        }
    }
})
