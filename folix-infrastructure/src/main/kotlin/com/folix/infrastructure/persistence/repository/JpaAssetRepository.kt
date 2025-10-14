package com.folix.infrastructure.persistence.repository

import com.folix.domain.asset.AssetType
import com.folix.infrastructure.persistence.entity.AssetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Asset용 Spring Data JPA 리포지토리.
 */
@Repository
interface JpaAssetRepository : JpaRepository<AssetEntity, UUID> {

    /**
     * 심볼로 자산을 조회한다.
     */
    fun findBySymbol(symbol: String): AssetEntity?

    /**
     * 자산 유형으로 조회한다.
     */
    fun findByType(type: AssetType): List<AssetEntity>

    /**
     * 심볼 존재 여부를 확인한다.
     */
    fun existsBySymbol(symbol: String): Boolean
}
