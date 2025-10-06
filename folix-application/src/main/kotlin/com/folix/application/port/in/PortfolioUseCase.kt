package com.folix.application.port.`in`

import com.folix.domain.money.Currency
import com.folix.domain.portfolio.Allocation
import com.folix.domain.portfolio.Portfolio
import com.folix.domain.portfolio.Position
import com.folix.domain.performance.PerformanceResult
import java.time.LocalDate

/**
 * 포트폴리오 조회 인바운드 포트.
 */
interface PortfolioUseCase {
    fun getPortfolio(baseCurrency: Currency = Currency.USD): Portfolio
    fun getPositions(): List<Position>
    fun getAllocation(groupBy: String = "type"): List<Allocation>
    fun getPerformance(from: LocalDate?, to: LocalDate?): PerformanceResult
}
