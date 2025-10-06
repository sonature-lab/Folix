package com.folix.application.port.out

import com.folix.domain.asset.Asset
import com.folix.domain.asset.AssetType
import java.util.UUID

/**
 * 자산 영속화를 위한 아웃바운드 포트.
 */
interface AssetRepository {
    fun save(asset: Asset): Asset
    fun findById(id: UUID): Asset?
    fun findBySymbol(symbol: String): Asset?
    fun findAll(): List<Asset>
    fun findByType(type: AssetType): List<Asset>
    fun existsBySymbol(symbol: String): Boolean
    fun deleteById(id: UUID)
}
