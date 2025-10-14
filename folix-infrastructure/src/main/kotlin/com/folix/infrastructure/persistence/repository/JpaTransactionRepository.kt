package com.folix.infrastructure.persistence.repository

import com.folix.infrastructure.persistence.entity.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

/**
 * Transaction용 Spring Data JPA 리포지토리.
 */
@Repository
interface JpaTransactionRepository : JpaRepository<TransactionEntity, UUID> {

    /**
     * 계좌 ID로 거래 내역을 조회한다.
     */
    fun findByAccountId(accountId: UUID): List<TransactionEntity>

    /**
     * 자산 ID로 거래 내역을 조회한다.
     */
    fun findByAssetId(assetId: UUID): List<TransactionEntity>

    /**
     * 날짜 범위로 거래 내역을 조회한다.
     */
    @Query("SELECT t FROM TransactionEntity t WHERE t.date BETWEEN :from AND :to ORDER BY t.date")
    fun findByDateRange(
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate
    ): List<TransactionEntity>

    /**
     * 계좌 ID와 날짜 범위로 거래 내역을 조회한다.
     */
    @Query("SELECT t FROM TransactionEntity t WHERE t.accountId = :accountId AND t.date BETWEEN :from AND :to ORDER BY t.date")
    fun findByAccountIdAndDateRange(
        @Param("accountId") accountId: UUID,
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate
    ): List<TransactionEntity>
}
