package com.folix.infrastructure.persistence.entity

import com.folix.domain.asset.Asset
import com.folix.domain.asset.AssetClass
import com.folix.domain.asset.AssetType
import com.folix.domain.money.Currency
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

/**
 * Asset JPA 엔티티.
 *
 * 도메인 [Asset]과 분리된 영속성 모델.
 */
@Entity
@Table(
    name = "assets",
    indexes = [
        Index(name = "idx_asset_symbol", columnList = "symbol"),
        Index(name = "idx_asset_type", columnList = "type")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_asset_symbol", columnNames = ["symbol"])
    ]
)
class AssetEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "symbol", nullable = false, unique = true, length = 50)
    var symbol: String,

    @Column(name = "name", nullable = false, length = 200)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    var type: AssetType,

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_class", nullable = false, length = 50)
    var assetClass: AssetClass,

    @Column(name = "currency", nullable = false, length = 10)
    var currency: String,

    @Column(name = "exchange", length = 100)
    var exchange: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {

    /**
     * JPA 엔티티를 도메인 객체로 변환한다.
     */
    fun toDomain(): Asset = Asset(
        id = id,
        symbol = symbol,
        name = name,
        type = type,
        assetClass = assetClass,
        currency = Currency.of(currency),
        exchange = exchange
    )

    companion object {
        /**
         * 도메인 객체로부터 JPA 엔티티를 생성한다.
         */
        fun fromDomain(domain: Asset): AssetEntity = AssetEntity(
            id = domain.id,
            symbol = domain.symbol,
            name = domain.name,
            type = domain.type,
            assetClass = domain.assetClass,
            currency = domain.currency.code,
            exchange = domain.exchange
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AssetEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "AssetEntity(id=$id, symbol='$symbol', name='$name')"
}
