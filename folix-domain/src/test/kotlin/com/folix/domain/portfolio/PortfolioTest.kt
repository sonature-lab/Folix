package com.folix.domain.portfolio

import com.folix.domain.money.Currency
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

class PortfolioTest : DescribeSpec({

    val applePosition = Position(
        assetId = UUID.randomUUID(),
        symbol = "AAPL",
        name = "Apple Inc.",
        quantity = BigDecimal("10"),
        averageCost = BigDecimal("150"),
        currentPrice = BigDecimal("175"),
        currency = Currency.USD
    )

    val googlePosition = Position(
        assetId = UUID.randomUUID(),
        symbol = "GOOGL",
        name = "Alphabet Inc.",
        quantity = BigDecimal("5"),
        averageCost = BigDecimal("140"),
        currentPrice = BigDecimal("160"),
        currency = Currency.USD
    )

    val closedPosition = Position(
        assetId = UUID.randomUUID(),
        symbol = "TSLA",
        name = "Tesla Inc.",
        quantity = BigDecimal.ZERO,
        averageCost = BigDecimal("200"),
        currentPrice = BigDecimal("250"),
        currency = Currency.USD
    )

    describe("Portfolio") {

        val portfolio = Portfolio(
            positions = listOf(applePosition, googlePosition, closedPosition),
            baseCurrency = Currency.USD
        )

        describe("포지션 필터링") {
            it("should_return_active_positions_only") {
                portfolio.activePositions shouldHaveSize 2
            }

            it("should_count_active_positions") {
                portfolio.positionCount shouldBe 2
            }
        }

        describe("총액 계산") {
            it("should_calculate_total_market_value") {
                // AAPL: 10 * 175 = 1750, GOOGL: 5 * 160 = 800
                portfolio.totalMarketValue.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("2550.00")
            }

            it("should_calculate_total_cost") {
                // AAPL: 10 * 150 = 1500, GOOGL: 5 * 140 = 700
                portfolio.totalCost.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("2200.00")
            }

            it("should_calculate_total_unrealized_pnl") {
                // 2550 - 2200 = 350
                portfolio.totalUnrealizedPnl.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("350.00")
            }

            it("should_calculate_total_return_rate") {
                // (350 / 2200) * 100 = 15.9091%
                portfolio.totalReturnRate.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("15.91")
            }
        }

        describe("자산 배분") {
            it("should_calculate_allocation_by_symbol") {
                val allocations = portfolio.allocationByType()
                allocations shouldHaveSize 2
                // AAPL: 1750/2550 = 68.63%, GOOGL: 800/2550 = 31.37%
                val aapl = allocations.first { it.category == "AAPL" }
                aapl.weight.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("68.63")
            }

            it("should_calculate_allocation_by_currency") {
                val allocations = portfolio.allocationByCurrency()
                allocations shouldHaveSize 1
                allocations[0].category shouldBe "USD"
                allocations[0].weight.setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("100.00")
            }
        }

        describe("빈 포트폴리오") {
            it("should_handle_empty_portfolio") {
                val empty = Portfolio(emptyList())
                empty.positionCount shouldBe 0
                empty.totalMarketValue shouldBe BigDecimal.ZERO
                empty.totalReturnRate shouldBe BigDecimal.ZERO
            }
        }
    }
})
