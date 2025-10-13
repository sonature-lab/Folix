package com.folix.infrastructure.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.*

/**
 * DailyPrice JPA 엔티티.
 *
 * 자산의 일별 종가 데이터를 저장한다.
 */
@Entity
@Table(
    name = "daily_prices",
    indexes = [
        Index(name = "idx_daily_price_asset", columnList = "asset_id"),
        Index(name = "idx_daily_price_date", columnList = "date"),
        Index(name = "idx_daily_price_asset_date", columnList = "asset_id,date")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_daily_price_asset_date", columnNames = ["asset_id", "date"])
    ]
)
class DailyPriceEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "asset_id", nullable = false)
    var assetId: UUID,

    @Column(name = "date", nullable = false)
    var date: LocalDate,

    @Column(name = "close", nullable = false, precision = 20, scale = 8)
    var close: BigDecimal,

    @Column(name = "source", nullable = false, length = 100)
    var source: String,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DailyPriceEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "DailyPriceEntity(assetId=$assetId, date=$date, close=$close)"
}
