package com.folix.infrastructure.persistence.adapter

import com.folix.application.port.out.AssetRepository
import com.folix.domain.asset.Asset
import com.folix.domain.asset.AssetType
import com.folix.infrastructure.persistence.entity.AssetEntity
import com.folix.infrastructure.persistence.repository.JpaAssetRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*

/**
 * [AssetRepository] 인터페이스 구현체.
 *
 * JPA를 통해 Asset 도메인 객체의 영속성을 담당한다.
 */
@Repository
class AssetRepositoryAdapter(
    private val jpaRepository: JpaAssetRepository
) : AssetRepository {

    override fun save(asset: Asset): Asset {
        val entity = AssetEntity.fromDomain(asset)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: UUID): Asset? {
        return jpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findBySymbol(symbol: String): Asset? {
        return jpaRepository.findBySymbol(symbol)?.toDomain()
    }

    override fun findAll(): List<Asset> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findByType(type: AssetType): List<Asset> {
        return jpaRepository.findByType(type).map { it.toDomain() }
    }

    override fun existsBySymbol(symbol: String): Boolean {
        return jpaRepository.existsBySymbol(symbol)
    }

    override fun deleteById(id: UUID) {
        jpaRepository.deleteById(id)
    }
}
