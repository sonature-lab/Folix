package com.folix.infrastructure.persistence.repository

import com.folix.infrastructure.persistence.entity.DailyPriceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

/**
 * DailyPrice용 Spring Data JPA 리포지토리.
 */
@Repository
interface JpaDailyPriceRepository : JpaRepository<DailyPriceEntity, UUID> {

    /**
     * 자산의 가장 최근 가격을 조회한다.
     */
    @Query("SELECT d FROM DailyPriceEntity d WHERE d.assetId = :assetId ORDER BY d.date DESC LIMIT 1")
    fun findLatestByAssetId(@Param("assetId") assetId: UUID): DailyPriceEntity?

    /**
     * 특정 날짜의 가격을 조회한다.
     */
    fun findByAssetIdAndDate(assetId: UUID, date: LocalDate): DailyPriceEntity?

    /**
     * 날짜 범위의 가격 데이터를 조회한다.
     */
    @Query("SELECT d FROM DailyPriceEntity d WHERE d.assetId = :assetId AND d.date BETWEEN :from AND :to ORDER BY d.date")
    fun findByAssetIdAndDateRange(
        @Param("assetId") assetId: UUID,
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate
    ): List<DailyPriceEntity>
}
