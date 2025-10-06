package com.folix.application.port.`in`

import com.folix.domain.asset.Asset
import com.folix.domain.asset.AssetType
import java.util.UUID

/**
 * 자산 관리 인바운드 포트.
 */
interface AssetUseCase {

    data class CreateAssetCommand(
        val symbol: String,
        val name: String,
        val type: AssetType,
        val currency: String,
        val exchange: String? = null
    )

    fun createAsset(command: CreateAssetCommand): Asset
    fun getAsset(id: UUID): Asset
    fun getAllAssets(): List<Asset>
    fun getAssetsByType(type: AssetType): List<Asset>
}
