package com.folix.application.service

import com.folix.application.port.`in`.PortfolioUseCase
import com.folix.application.port.out.AssetRepository
import com.folix.application.port.out.DailyPriceRepository
import com.folix.application.port.out.TransactionRepository
import com.folix.domain.money.Currency
import com.folix.domain.performance.CashFlow
import com.folix.domain.performance.DailyValue
import com.folix.domain.performance.PerformanceResult
import com.folix.domain.performance.RiskMetrics
import com.folix.domain.performance.TimeWeightedReturn
import com.folix.domain.performance.InternalRateOfReturn
import com.folix.domain.portfolio.Allocation
import com.folix.domain.portfolio.Portfolio
import com.folix.domain.portfolio.Position
import com.folix.domain.transaction.TransactionType
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * 포트폴리오 조회 유즈케이스 구현.
 *
 * 거래 내역에서 포지션을 계산하고, 시세 데이터로 현재 가치를 평가한다.
 */
class PortfolioService(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
    private val dailyPriceRepository: DailyPriceRepository
) : PortfolioUseCase {

    override fun getPortfolio(baseCurrency: Currency): Portfolio {
        val positions = getPositions()
        return Portfolio(positions = positions, baseCurrency = baseCurrency)
    }

    override fun getPositions(): List<Position> {
        val transactions = transactionRepository.findAll()
        if (transactions.isEmpty()) return emptyList()

        return transactions
            .groupBy { it.assetId }
            .mapNotNull { (assetId, txns) ->
                val asset = assetRepository.findById(assetId) ?: return@mapNotNull null

                var totalQuantity = BigDecimal.ZERO
                var totalCost = BigDecimal.ZERO

                for (txn in txns.sortedBy { it.date }) {
                    when (txn.type) {
                        TransactionType.BUY -> {
                            totalCost = totalCost.add(txn.totalAmount)
                            totalQuantity = totalQuantity.add(txn.quantity)
                        }
                        TransactionType.SELL -> {
                            if (totalQuantity > BigDecimal.ZERO) {
                                val avgCost = totalCost.divide(totalQuantity, 8, RoundingMode.HALF_UP)
                                totalCost = totalCost.subtract(avgCost.multiply(txn.quantity))
                            }
                            totalQuantity = totalQuantity.subtract(txn.quantity)
                        }
                        else -> { /* DIVIDEND, INTEREST, DEPOSIT, WITHDRAWAL — 포지션 수량에 영향 없음 */ }
                    }
                }

                if (totalQuantity.compareTo(BigDecimal.ZERO) == 0) return@mapNotNull null

                val avgCost = if (totalQuantity > BigDecimal.ZERO) {
                    totalCost.divide(totalQuantity, 8, RoundingMode.HALF_UP)
                } else BigDecimal.ZERO

                val currentPrice = dailyPriceRepository.findLatestPrice(assetId)
                    ?: avgCost

                Position(
                    assetId = assetId,
                    symbol = asset.symbol,
                    name = asset.name,
                    quantity = totalQuantity,
                    averageCost = avgCost,
                    currentPrice = currentPrice,
                    currency = asset.currency
                )
            }
    }

    override fun getAllocation(groupBy: String): List<Allocation> {
        val positions = getPositions()
        if (positions.isEmpty()) return emptyList()

        val groupFn: (Position) -> String = when (groupBy.lowercase()) {
            "currency" -> { p -> p.currency.code }
            "type" -> { p ->
                val asset = assetRepository.findById(p.assetId)
                asset?.type?.name ?: "UNKNOWN"
            }
            else -> { p ->
                val asset = assetRepository.findById(p.assetId)
                asset?.type?.name ?: "UNKNOWN"
            }
        }

        return Allocation.fromPositions(positions, groupFn)
    }

    override fun getPerformance(from: LocalDate?, to: LocalDate?): PerformanceResult {
        val periodEnd = to ?: LocalDate.now()
        val periodStart = from ?: periodEnd.minusYears(1)

        val transactions = transactionRepository.findByDateRange(periodStart, periodEnd)

        val cashFlows = transactions.map { txn ->
            val amount = when {
                txn.type.isOutflow -> txn.netAmount.negate()
                txn.type.isInflow -> txn.netAmount
                else -> txn.netAmount
            }
            CashFlow(date = txn.date, amount = amount)
        }

        val dailyValues = generateDailyValues(periodStart, periodEnd)

        val twr = if (dailyValues.size >= 2) {
            TimeWeightedReturn.calculate(dailyValues, cashFlows)
        } else BigDecimal.ZERO

        val irr = if (cashFlows.isNotEmpty()) {
            InternalRateOfReturn.calculate(cashFlows)
        } else null

        val riskResult = if (dailyValues.size >= 2) {
            RiskMetrics.calculate(dailyValues, twr)
        } else null

        return PerformanceResult(
            twr = twr,
            irr = irr,
            annualizedVolatility = riskResult?.annualizedVolatility ?: BigDecimal.ZERO,
            sharpeRatio = riskResult?.sharpeRatio ?: BigDecimal.ZERO,
            maxDrawdown = riskResult?.maxDrawdown ?: BigDecimal.ZERO,
            periodStart = periodStart,
            periodEnd = periodEnd
        )
    }

    private fun generateDailyValues(from: LocalDate, to: LocalDate): List<DailyValue> {
        val positions = getPositions()
        if (positions.isEmpty()) return emptyList()

        val dates = generateSequence(from) { it.plusDays(1) }
            .takeWhile { !it.isAfter(to) }
            .toList()

        return dates.mapNotNull { date ->
            val totalValue = positions.sumOf { pos ->
                val price = dailyPriceRepository.findPriceAt(pos.assetId, date)
                    ?: return@mapNotNull null
                pos.quantity.multiply(price)
            }
            DailyValue(date = date, value = totalValue)
        }
    }
}
