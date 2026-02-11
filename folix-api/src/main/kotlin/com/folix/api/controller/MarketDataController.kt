package com.folix.api.controller

import com.folix.api.common.ApiResponse
import com.folix.api.dto.ExchangeRateResponse
import com.folix.api.dto.PriceResponse
import com.folix.application.port.`in`.MarketDataUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
 * 시장 데이터 REST API 컨트롤러.
 *
 * 시세 조회 및 환율 조회 기능을 제공한다.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Market Data", description = "시장 데이터 조회 API")
class MarketDataController(
    private val marketDataUseCase: MarketDataUseCase
) {

    /**
     * 자산 현재 시세 조회.
     */
    @GetMapping("/prices/{symbol}")
    @Operation(summary = "시세 조회", description = "자산 심볼로 현재 시세를 조회합니다")
    fun getPrice(@PathVariable symbol: String): ApiResponse<PriceResponse> {
        val result = marketDataUseCase.getPrice(symbol)
        return ApiResponse.ok(PriceResponse.from(result))
    }

    /**
     * 환율 조회.
     */
    @GetMapping("/exchange-rates")
    @Operation(summary = "환율 조회", description = "통화쌍 환율을 조회합니다")
    fun getExchangeRate(
        @RequestParam from: String,
        @RequestParam to: String,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): ApiResponse<ExchangeRateResponse> {
        val result = marketDataUseCase.getExchangeRate(from, to, date)
        return ApiResponse.ok(ExchangeRateResponse.from(result))
    }
}
