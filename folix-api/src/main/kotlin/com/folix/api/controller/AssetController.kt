package com.folix.api.controller

import com.folix.api.common.ApiResponse
import com.folix.api.dto.AssetResponse
import com.folix.api.dto.CreateAssetRequest
import com.folix.application.port.`in`.AssetUseCase
import com.folix.domain.asset.AssetType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * 자산 관리 REST API 컨트롤러.
 *
 * 자산 등록, 조회 기능을 제공한다.
 */
@RestController
@RequestMapping("/api/v1/assets")
@Tag(name = "Assets", description = "자산 관리 API")
class AssetController(
    private val assetUseCase: AssetUseCase
) {

    /**
     * 자산 등록.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "자산 등록", description = "새로운 자산을 등록합니다")
    fun createAsset(@RequestBody request: CreateAssetRequest): ApiResponse<AssetResponse> {
        val command = AssetUseCase.CreateAssetCommand(
            symbol = request.symbol,
            name = request.name,
            type = request.type,
            currency = request.currency,
            exchange = request.exchange
        )
        val asset = assetUseCase.createAsset(command)
        return ApiResponse.ok(AssetResponse.from(asset))
    }

    /**
     * 자산 목록 조회.
     */
    @GetMapping
    @Operation(summary = "자산 목록 조회", description = "전체 자산 목록을 조회합니다")
    fun getAllAssets(
        @RequestParam(required = false) type: String?
    ): ApiResponse<List<AssetResponse>> {
        val assets = if (type != null) {
            val assetType = try {
                AssetType.valueOf(type.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException(
                    "Invalid asset type: '$type'. Valid values: ${AssetType.entries.joinToString()}"
                )
            }
            assetUseCase.getAssetsByType(assetType)
        } else {
            assetUseCase.getAllAssets()
        }
        return ApiResponse.ok(assets.map { AssetResponse.from(it) })
    }

    /**
     * 자산 단건 조회.
     */
    @GetMapping("/{id}")
    @Operation(summary = "자산 조회", description = "ID로 특정 자산을 조회합니다")
    fun getAsset(@PathVariable id: String): ApiResponse<AssetResponse> {
        val asset = assetUseCase.getAsset(UUID.fromString(id))
        return ApiResponse.ok(AssetResponse.from(asset))
    }
}
