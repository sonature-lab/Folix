package com.folix.api.dto

import com.folix.domain.asset.Asset
import com.folix.domain.asset.AssetType

/**
 * 자산 생성 요청 DTO.
 */
data class CreateAssetRequest(
    val symbol: String,
    val name: String,
    val type: AssetType,
    val currency: String,
    val exchange: String? = null
)

/**
 * 자산 응답 DTO.
 */
data class AssetResponse(
    val id: String,
    val symbol: String,
    val name: String,
    val type: String,
    val currency: String,
    val exchange: String?
) {
    companion object {
        /**
         * 도메인 Asset을 AssetResponse로 변환.
         */
        fun from(asset: Asset): AssetResponse = AssetResponse(
            id = asset.id.toString(),
            symbol = asset.symbol,
            name = asset.name,
            type = asset.type.name,
            currency = asset.currency.code,
            exchange = asset.exchange
        )
    }
}
