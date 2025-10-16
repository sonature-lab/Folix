package com.folix.api.dto

import com.folix.domain.performance.PerformanceResult
import com.folix.domain.portfolio.Allocation
import com.folix.domain.portfolio.Portfolio
import com.folix.domain.portfolio.Position
import java.time.LocalDate

/**
 * 포트폴리오 요약 응답 DTO.
 */
data class PortfolioSummaryResponse(
    val totalValue: String,
    val totalCost: String,
    val totalReturn: String,
    val returnRate: String,
    val baseCurrency: String,
    val positionCount: Int
) {
    companion object {
        /**
         * 도메인 Portfolio를 PortfolioSummaryResponse로 변환.
         */
        fun from(portfolio: Portfolio): PortfolioSummaryResponse = PortfolioSummaryResponse(
            totalValue = portfolio.totalMarketValue.toPlainString(),
            totalCost = portfolio.totalCost.toPlainString(),
            totalReturn = portfolio.totalUnrealizedPnl.toPlainString(),
            returnRate = portfolio.totalReturnRate.toPlainString(),
            baseCurrency = portfolio.baseCurrency.code,
            positionCount = portfolio.positionCount
        )
    }
}

/**
 * 포지션 응답 DTO.
 */
data class PositionResponse(
    val assetId: String,
    val symbol: String,
    val name: String,
    val quantity: String,
    val avgCost: String,
    val currentPrice: String,
    val marketValue: String,
    val unrealizedPnl: String,
    val returnRate: String,
    val currency: String
) {
    companion object {
        fun from(position: Position): PositionResponse = PositionResponse(
            assetId = position.assetId.toString(),
            symbol = position.symbol,
            name = position.name,
            quantity = position.quantity.toPlainString(),
            avgCost = position.averageCost.toPlainString(),
            currentPrice = position.currentPrice.toPlainString(),
            marketValue = position.marketValue.toPlainString(),
            unrealizedPnl = position.unrealizedPnl.toPlainString(),
            returnRate = position.returnRate.toPlainString(),
            currency = position.currency.code
        )
    }
}

/**
 * 자산 배분 응답 DTO.
 */
data class AllocationResponse(
    val category: String,
    val value: String,
    val weight: String
) {
    companion object {
        fun from(allocation: Allocation): AllocationResponse = AllocationResponse(
            category = allocation.category,
            value = allocation.value.toPlainString(),
            weight = allocation.weight.toPlainString()
        )
    }
}

/**
 * 성과 응답 DTO.
 */
data class PerformanceResponse(
    val twr: String,
    val irr: String?,
    val volatility: String,
    val sharpeRatio: String,
    val maxDrawdown: String,
    val period: PeriodResponse
) {
    companion object {
        /**
         * 도메인 PerformanceResult를 PerformanceResponse로 변환.
         */
        fun from(result: PerformanceResult): PerformanceResponse = PerformanceResponse(
            twr = result.twrPercent.toPlainString(),
            irr = result.irrPercent?.toPlainString(),
            volatility = result.volatilityPercent.toPlainString(),
            sharpeRatio = result.sharpeRatio.toPlainString(),
            maxDrawdown = result.maxDrawdownPercent.toPlainString(),
            period = PeriodResponse(result.periodStart, result.periodEnd)
        )
    }
}

/**
 * 기간 응답 DTO.
 */
data class PeriodResponse(
    val from: LocalDate,
    val to: LocalDate
)
