package com.folix.domain.portfolio

import com.folix.domain.money.Currency
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

class PositionTest : DescribeSpec({

    describe("Position") {

        val position = Position(
            assetId = UUID.randomUUID(),
            symbol = "AAPL",
            name = "Apple Inc.",
            quantity = BigDecimal("10"),
            averageCost = BigDecimal("150"),
            currentPrice = BigDecimal("175"),
            currency = Currency.USD
        )

        describe("계산") {
            it("should_calculate_total_cost") {
                position.totalCost.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("1500.00")
            }

            it("should_calculate_market_value") {
                position.marketValue.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("1750.00")
            }

            it("should_calculate_unrealized_pnl") {
                position.unrealizedPnl.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("250.00")
            }

            it("should_calculate_return_rate") {
                // (250 / 1500) * 100 = 16.6667%
                position.returnRate.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("16.67")
            }

            it("should_return_zero_rate_when_cost_is_zero") {
                val zeroCost = position.copy(averageCost = BigDecimal.ZERO)
                zeroCost.returnRate shouldBe BigDecimal.ZERO
            }
        }

        describe("Money 변환") {
            it("should_return_market_value_as_money") {
                val money = position.marketValueAsMoney()
                money.currency shouldBe Currency.USD
                money.amount.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("1750.00")
            }

            it("should_return_unrealized_pnl_as_money") {
                val money = position.unrealizedPnlAsMoney()
                money.currency shouldBe Currency.USD
                money.amount.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("250.00")
            }
        }

        describe("상태") {
            it("should_detect_closed_position") {
                val closed = position.copy(quantity = BigDecimal.ZERO)
                closed.isClosed shouldBe true
            }

            it("should_detect_open_position") {
                position.isClosed shouldBe false
            }
        }

        describe("유효성 검증") {
            it("should_fail_when_quantity_is_negative") {
                shouldThrow<IllegalArgumentException> {
                    position.copy(quantity = BigDecimal("-1"))
                }
            }
        }
    }
})
