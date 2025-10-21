package com.folix.api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.folix.api.common.GlobalExceptionHandler
import com.folix.api.dto.CreateAssetRequest
import com.folix.application.exception.DuplicateEntityException
import com.folix.application.port.`in`.AssetUseCase
import com.folix.domain.asset.Asset
import com.folix.domain.asset.AssetType
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AssetControllerTest : DescribeSpec({

    val assetUseCase = mockk<AssetUseCase>()
    val controller = AssetController(assetUseCase)
    val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(GlobalExceptionHandler())
        .build()
    val objectMapper = jacksonObjectMapper()

    val apple = Asset.stock("AAPL", "Apple Inc.", exchange = "NASDAQ")

    describe("POST /api/v1/assets") {
        it("should_create_asset_and_return_201") {
            val request = CreateAssetRequest(
                symbol = "AAPL", name = "Apple Inc.",
                type = AssetType.STOCK, currency = "USD", exchange = "NASDAQ"
            )
            every { assetUseCase.createAsset(any()) } returns apple

            mockMvc.post("/api/v1/assets") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data.symbol") { value("AAPL") }
                jsonPath("$.data.type") { value("STOCK") }
            }
        }

        it("should_return_409_when_duplicate_symbol") {
            val request = CreateAssetRequest(
                symbol = "AAPL", name = "Apple",
                type = AssetType.STOCK, currency = "USD"
            )
            every { assetUseCase.createAsset(any()) } throws
                DuplicateEntityException("Asset", "symbol", "AAPL")

            mockMvc.post("/api/v1/assets") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isConflict() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error.code") { value("RES_002") }
            }
        }
    }

    describe("GET /api/v1/assets") {
        it("should_return_all_assets") {
            every { assetUseCase.getAllAssets() } returns listOf(apple)

            mockMvc.get("/api/v1/assets")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.length()") { value(1) }
                    jsonPath("$.data[0].symbol") { value("AAPL") }
                }
        }

        it("should_filter_by_type") {
            every { assetUseCase.getAssetsByType(AssetType.STOCK) } returns listOf(apple)

            mockMvc.get("/api/v1/assets") {
                param("type", "STOCK")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(1) }
            }
        }

        it("should_return_400_for_invalid_type") {
            mockMvc.get("/api/v1/assets") {
                param("type", "INVALID")
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.success") { value(false) }
            }
        }
    }

    describe("GET /api/v1/assets/{id}") {
        it("should_return_asset_by_id") {
            every { assetUseCase.getAsset(apple.id) } returns apple

            mockMvc.get("/api/v1/assets/${apple.id}")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.symbol") { value("AAPL") }
                }
        }
    }
})
