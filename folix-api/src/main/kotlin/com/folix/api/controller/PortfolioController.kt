package com.folix.api.controller

import com.folix.api.common.ApiResponse
import com.folix.api.dto.AllocationResponse
import com.folix.api.dto.PerformanceResponse
import com.folix.api.dto.PortfolioSummaryResponse
import com.folix.api.dto.PositionResponse
import com.folix.application.port.`in`.PortfolioUseCase
import com.folix.domain.money.Currency
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
 * 포트폴리오 REST API 컨트롤러.
 *
 * 포트폴리오 요약, 포지션, 배분, 성과 조회 기능을 제공한다.
 */
@RestController
@RequestMapping("/api/v1/portfolio")
@Tag(name = "Portfolio", description = "포트폴리오 API")
class PortfolioController(
    private val portfolioUseCase: PortfolioUseCase
) {

    /**
     * 포트폴리오 요약 조회.
     */
    @GetMapping
    @Operation(summary = "포트폴리오 요약", description = "포트폴리오 전체 요약을 조회합니다")
    fun getPortfolioSummary(
        @RequestParam(required = false, defaultValue = "USD") baseCurrency: String
    ): ApiResponse<PortfolioSummaryResponse> {
        val portfolio = portfolioUseCase.getPortfolio(Currency.of(baseCurrency))
        return ApiResponse.ok(PortfolioSummaryResponse.from(portfolio))
    }

    /**
     * 보유 포지션 목록 조회.
     */
    @GetMapping("/positions")
    @Operation(summary = "보유 포지션", description = "보유 중인 포지션 목록을 조회합니다")
    fun getPositions(): ApiResponse<List<PositionResponse>> {
        val positions = portfolioUseCase.getPositions()
        return ApiResponse.ok(positions.map { PositionResponse.from(it) })
    }

    /**
     * 자산 배분 조회.
     */
    @GetMapping("/allocation")
    @Operation(summary = "자산 배분", description = "자산 배분 현황을 조회합니다")
    fun getAllocation(
        @RequestParam(required = false, defaultValue = "type") groupBy: String
    ): ApiResponse<List<AllocationResponse>> {
        val allocation = portfolioUseCase.getAllocation(groupBy)
        return ApiResponse.ok(allocation.map { AllocationResponse.from(it) })
    }

    /**
     * 성과 조회.
     */
    @GetMapping("/performance")
    @Operation(summary = "성과 조회", description = "포트폴리오 성과 (TWR/IRR)를 조회합니다")
    fun getPerformance(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?
    ): ApiResponse<PerformanceResponse> {
        val performance = portfolioUseCase.getPerformance(from, to)
        return ApiResponse.ok(PerformanceResponse.from(performance))
    }
}
