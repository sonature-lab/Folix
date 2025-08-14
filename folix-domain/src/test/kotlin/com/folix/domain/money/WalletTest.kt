package com.folix.domain.money

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class WalletTest : DescribeSpec({

    describe("Wallet") {

        describe("생성") {
            it("should_create_empty_wallet") {
                val wallet = Wallet.empty()
                wallet.isEmpty shouldBe true
                wallet.size shouldBe 0
            }

            it("should_create_with_initial_money") {
                val wallet = Wallet.of(100.USD, "0.5".BTC)
                wallet.size shouldBe 2
                wallet[Currency.USD].amount shouldBe BigDecimal("100")
                wallet[Currency.BTC].amount shouldBe BigDecimal("0.5")
            }
        }

        describe("입금") {
            it("should_deposit_new_currency") {
                val wallet = Wallet.empty().deposit(100.USD)
                wallet[Currency.USD].amount shouldBe BigDecimal("100")
            }

            it("should_accumulate_same_currency") {
                val wallet = Wallet.empty()
                    .deposit(100.USD)
                    .deposit(50.USD)
                wallet[Currency.USD].amount shouldBe BigDecimal("150")
                wallet.size shouldBe 1
            }

            it("should_hold_multiple_currencies") {
                val wallet = Wallet.empty()
                    .deposit(100.USD)
                    .deposit(5_000_000.KRW)
                    .deposit("0.5".BTC)
                wallet.size shouldBe 3
            }
        }

        describe("출금") {
            it("should_withdraw_from_existing") {
                val wallet = Wallet.empty()
                    .deposit(100.USD)
                    .withdraw(30.USD)
                wallet[Currency.USD].amount shouldBe BigDecimal("70")
            }
        }

        describe("미보유 통화 조회") {
            it("should_return_zero_for_missing_currency") {
                val wallet = Wallet.empty()
                wallet[Currency.EUR].amount shouldBe BigDecimal.ZERO
                wallet[Currency.EUR].currency shouldBe Currency.EUR
            }
        }

        describe("기준 통화 합산") {
            it("should_sum_all_in_target_currency") {
                val wallet = Wallet.of(100.USD, 1_350_000.KRW)

                val usdKrw = ExchangeRate(
                    base = Currency.KRW,
                    quote = Currency.USD,
                    rate = BigDecimal("0.00074074")
                )

                val total = wallet.totalIn(Currency.USD) { from, to ->
                    when {
                        from == Currency.KRW && to == Currency.USD -> usdKrw
                        else -> throw IllegalArgumentException("No rate for $from/$to")
                    }
                }

                total.currency shouldBe Currency.USD
                // 100 + (1,350,000 * 0.00074074) ≈ 100 + 999.999 ≈ 1100
                total.amount.setScale(0, RoundingMode.HALF_UP) shouldBe BigDecimal("1100")
            }
        }

        describe("toList") {
            it("should_return_all_holdings") {
                val wallet = Wallet.of(100.USD, 200.EUR)
                wallet.toList().size shouldBe 2
            }
        }
    }
})
