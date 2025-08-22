package com.folix.domain.transaction

import com.folix.domain.money.Currency
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

class TransactionTest : DescribeSpec({

    val accountId = UUID.randomUUID()
    val assetId = UUID.randomUUID()

    describe("Transaction") {

        describe("생성") {
            it("should_create_buy_transaction") {
                val tx = Transaction(
                    accountId = accountId,
                    assetId = assetId,
                    type = TransactionType.BUY,
                    quantity = BigDecimal("10"),
                    price = BigDecimal("150.25"),
                    fee = BigDecimal("9.99"),
                    currency = Currency.USD,
                    date = LocalDate.of(2026, 1, 15)
                )
                tx.type shouldBe TransactionType.BUY
                tx.quantity shouldBe BigDecimal("10")
                tx.price shouldBe BigDecimal("150.25")
            }

            it("should_fail_when_quantity_is_zero") {
                shouldThrow<IllegalArgumentException> {
                    Transaction(
                        accountId = accountId, assetId = assetId,
                        type = TransactionType.BUY, quantity = BigDecimal.ZERO,
                        price = BigDecimal("100"), currency = Currency.USD,
                        date = LocalDate.now()
                    )
                }
            }

            it("should_fail_when_price_is_negative") {
                shouldThrow<IllegalArgumentException> {
                    Transaction(
                        accountId = accountId, assetId = assetId,
                        type = TransactionType.BUY, quantity = BigDecimal("1"),
                        price = BigDecimal("-100"), currency = Currency.USD,
                        date = LocalDate.now()
                    )
                }
            }

            it("should_fail_when_fee_is_negative") {
                shouldThrow<IllegalArgumentException> {
                    Transaction(
                        accountId = accountId, assetId = assetId,
                        type = TransactionType.BUY, quantity = BigDecimal("1"),
                        price = BigDecimal("100"), fee = BigDecimal("-1"),
                        currency = Currency.USD, date = LocalDate.now()
                    )
                }
            }
        }

        describe("총액 계산") {
            it("should_calculate_total_amount") {
                val tx = Transaction(
                    accountId = accountId, assetId = assetId,
                    type = TransactionType.BUY,
                    quantity = BigDecimal("10"),
                    price = BigDecimal("150.25"),
                    currency = Currency.USD,
                    date = LocalDate.now()
                )
                tx.totalAmount.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("1502.50")
            }

            it("should_calculate_net_amount_for_buy") {
                val tx = Transaction(
                    accountId = accountId, assetId = assetId,
                    type = TransactionType.BUY,
                    quantity = BigDecimal("10"),
                    price = BigDecimal("100"),
                    fee = BigDecimal("10"),
                    currency = Currency.USD,
                    date = LocalDate.now()
                )
                // BUY: totalAmount + fee = 1000 + 10 = 1010
                tx.netAmount.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("1010.00")
            }

            it("should_calculate_net_amount_for_sell") {
                val tx = Transaction(
                    accountId = accountId, assetId = assetId,
                    type = TransactionType.SELL,
                    quantity = BigDecimal("10"),
                    price = BigDecimal("100"),
                    fee = BigDecimal("10"),
                    currency = Currency.USD,
                    date = LocalDate.now()
                )
                // SELL: totalAmount - fee = 1000 - 10 = 990
                tx.netAmount.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("990.00")
            }
        }

        describe("Money 변환") {
            it("should_return_total_as_money") {
                val tx = Transaction(
                    accountId = accountId, assetId = assetId,
                    type = TransactionType.BUY,
                    quantity = BigDecimal("5"),
                    price = BigDecimal("200"),
                    currency = Currency.USD,
                    date = LocalDate.now()
                )
                val money = tx.totalAsMoney()
                money.currency shouldBe Currency.USD
                money.amount.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("1000.00")
            }
        }
    }

    describe("TransactionType") {
        it("should_classify_inflow") {
            TransactionType.BUY.isInflow shouldBe true
            TransactionType.DEPOSIT.isInflow shouldBe true
            TransactionType.SELL.isInflow shouldBe false
        }

        it("should_classify_outflow") {
            TransactionType.SELL.isOutflow shouldBe true
            TransactionType.WITHDRAWAL.isOutflow shouldBe true
            TransactionType.BUY.isOutflow shouldBe false
        }

        it("should_classify_income") {
            TransactionType.DIVIDEND.isIncome shouldBe true
            TransactionType.INTEREST.isIncome shouldBe true
            TransactionType.BUY.isIncome shouldBe false
        }
    }
})
