package com.folix.infrastructure.persistence.repository

import com.folix.infrastructure.persistence.entity.ExchangeRateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

/**
 * ExchangeRate용 Spring Data JPA 리포지토리.
 */
@Repository
interface JpaExchangeRateRepository : JpaRepository<ExchangeRateEntity, UUID> {

    /**
     * 통화 쌍과 날짜로 환율을 조회한다.
     */
    fun findByBaseCurrencyAndQuoteCurrencyAndDate(
        baseCurrency: String,
        quoteCurrency: String,
        date: LocalDate
    ): ExchangeRateEntity?

    /**
     * 통화 쌍의 가장 최근 환율을 조회한다.
     */
    @Query("""
        SELECT e FROM ExchangeRateEntity e
        WHERE e.baseCurrency = :baseCurrency
        AND e.quoteCurrency = :quoteCurrency
        ORDER BY e.date DESC
        LIMIT 1
    """)
    fun findLatestByBaseCurrencyAndQuoteCurrency(
        @Param("baseCurrency") baseCurrency: String,
        @Param("quoteCurrency") quoteCurrency: String
    ): ExchangeRateEntity?
}
