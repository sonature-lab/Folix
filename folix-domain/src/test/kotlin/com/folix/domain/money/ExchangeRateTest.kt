package com.folix.domain.money

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class ExchangeRateTest : DescribeSpec({

    describe("ExchangeRate") {

        val usdKrw = ExchangeRate(
            base = Currency.USD,
            quote = Currency.KRW,
            rate = BigDecimal("1350.00"),
            date = LocalDate.of(2026, 2, 11)
        )

        describe("생성") {
            it("should_create_with_valid_params") {
                usdKrw.base shouldBe Currency.USD
                usdKrw.quote shouldBe Currency.KRW
                usdKrw.rate shouldBe BigDecimal("1350.00")
            }

            it("should_fail_when_same_currency") {
                shouldThrow<IllegalArgumentException> {
                    ExchangeRate(Currency.USD, Currency.USD, BigDecimal("1"))
                }.message shouldContain "must differ"
            }

            it("should_fail_when_rate_is_zero") {
                shouldThrow<IllegalArgumentException> {
                    ExchangeRate(Currency.USD, Currency.KRW, BigDecimal.ZERO)
                }.message shouldContain "positive"
            }

            it("should_fail_when_rate_is_negative") {
                shouldThrow<IllegalArgumentException> {
                    ExchangeRate(Currency.USD, Currency.KRW, BigDecimal("-1"))
                }
            }
        }

        describe("환율 변환") {
            it("should_convert_base_to_quote") {
                val usd100 = Money(BigDecimal("100"), Currency.USD)
                val result = usdKrw.convert(usd100)
                result.currency shouldBe Currency.KRW
                result.amount.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("135000.00")
            }

            it("should_fail_when_currency_mismatch") {
                val eur = Money(BigDecimal("100"), Currency.EUR)
                shouldThrow<IllegalArgumentException> {
                    usdKrw.convert(eur)
                }.message shouldContain "Cannot convert"
            }
        }

        describe("역환율") {
            it("should_invert_rate") {
                val krwUsd = usdKrw.invert()
                krwUsd.base shouldBe Currency.KRW
                krwUsd.quote shouldBe Currency.USD
                // 1 / 1350 ≈ 0.0007407407
                krwUsd.rate.toDouble() shouldBe 0.0007407407.toBigDecimal()
                    .setScale(10, RoundingMode.HALF_UP).toDouble()
            }

            it("should_convert_with_inverted_rate") {
                val krwUsd = usdKrw.invert()
                val krw = Money(BigDecimal("1350000"), Currency.KRW)
                val result = krwUsd.convert(krw)
                result.currency shouldBe Currency.USD
                // 1,350,000 * 0.0007407407 ≈ 1000
                result.amount.setScale(0, RoundingMode.HALF_UP) shouldBe BigDecimal("1000")
            }
        }

        describe("toString") {
            it("should_format_properly") {
                usdKrw.toString() shouldContain "USD/KRW"
                usdKrw.toString() shouldContain "1350.00"
            }
        }
    }
})
