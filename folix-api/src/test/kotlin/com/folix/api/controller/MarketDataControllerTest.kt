package com.folix.api.controller

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.folix.api.common.GlobalExceptionHandler
import com.folix.application.exception.ExternalApiException
import com.folix.application.exception.MarketDataNotFoundException
import com.folix.application.port.`in`.MarketDataUseCase
import com.folix.application.port.`in`.MarketDataUseCase.ExchangeRateResult
import com.folix.application.port.`in`.MarketDataUseCase.PriceResult
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDate

class MarketDataControllerTest : DescribeSpec({

    val marketDataUseCase = mockk<MarketDataUseCase>()
    val controller = MarketDataController(marketDataUseCase)

    val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
        .build()

    describe("GET /api/v1/prices/{symbol}") {
        it("should_return_price_for_valid_symbol") {
            val priceResult = PriceResult(
                symbol = "AAPL",
                price = BigDecimal("150.50"),
                currency = "USD",
                source = "YahooFinance",
                date = LocalDate.of(2026, 2, 11)
            )
            every { marketDataUseCase.getPrice("AAPL") } returns priceResult

            mockMvc.get("/api/v1/prices/AAPL")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.success") { value(true) }
                    jsonPath("$.data.symbol") { value("AAPL") }
                    jsonPath("$.data.price") { value(150.50) }
                    jsonPath("$.data.currency") { value("USD") }
                    jsonPath("$.data.source") { value("YahooFinance") }
                }
        }

        it("should_return_404_when_symbol_not_found") {
            every { marketDataUseCase.getPrice("UNKNOWN") } throws
                MarketDataNotFoundException("UNKNOWN")

            mockMvc.get("/api/v1/prices/UNKNOWN")
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.success") { value(false) }
                    jsonPath("$.error.code") { value("MKT_001") }
                }
        }

        it("should_return_502_when_external_api_fails") {
            every { marketDataUseCase.getPrice("AAPL") } throws
                ExternalApiException("NONE", "No provider supports asset type: UNKNOWN")

            mockMvc.get("/api/v1/prices/AAPL")
                .andExpect {
                    status { isBadGateway() }
                    jsonPath("$.success") { value(false) }
                    jsonPath("$.error.code") { value("EXT_001") }
                }
        }
    }

    describe("GET /api/v1/exchange-rates") {
        it("should_return_exchange_rate") {
            val rateResult = ExchangeRateResult(
                baseCurrency = "USD",
                quoteCurrency = "KRW",
                rate = BigDecimal("1350.50"),
                date = LocalDate.of(2026, 2, 11),
                source = "ECB"
            )
            every { marketDataUseCase.getExchangeRate("USD", "KRW", null) } returns rateResult

            mockMvc.get("/api/v1/exchange-rates") {
                param("from", "USD")
                param("to", "KRW")
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data.baseCurrency") { value("USD") }
                jsonPath("$.data.quoteCurrency") { value("KRW") }
                jsonPath("$.data.rate") { value(1350.50) }
                jsonPath("$.data.source") { value("ECB") }
            }
        }

        it("should_return_exchange_rate_with_date") {
            val date = LocalDate.of(2026, 1, 15)
            val rateResult = ExchangeRateResult(
                baseCurrency = "USD",
                quoteCurrency = "KRW",
                rate = BigDecimal("1340.00"),
                date = date,
                source = "ECB"
            )
            every { marketDataUseCase.getExchangeRate("USD", "KRW", date) } returns rateResult

            mockMvc.get("/api/v1/exchange-rates") {
                param("from", "USD")
                param("to", "KRW")
                param("date", "2026-01-15")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.date") { value("2026-01-15") }
            }
        }

        it("should_return_404_when_rate_not_found") {
            every { marketDataUseCase.getExchangeRate("USD", "XXX", null) } throws
                MarketDataNotFoundException("USD/XXX")

            mockMvc.get("/api/v1/exchange-rates") {
                param("from", "USD")
                param("to", "XXX")
            }.andExpect {
                status { isNotFound() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error.code") { value("MKT_001") }
            }
        }
    }
})
