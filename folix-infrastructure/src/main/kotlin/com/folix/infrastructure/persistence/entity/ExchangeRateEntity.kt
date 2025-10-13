package com.folix.infrastructure.persistence.entity

import com.folix.domain.money.Currency
import com.folix.domain.money.ExchangeRate
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.*

/**
 * ExchangeRate JPA 엔티티.
 *
 * 도메인 [ExchangeRate]와 분리된 영속성 모델.
 */
@Entity
@Table(
    name = "exchange_rates",
    indexes = [
        Index(name = "idx_exchange_rate_pair", columnList = "base_currency,quote_currency"),
        Index(name = "idx_exchange_rate_date", columnList = "date"),
        Index(name = "idx_exchange_rate_pair_date", columnList = "base_currency,quote_currency,date")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_exchange_rate_pair_date",
            columnNames = ["base_currency", "quote_currency", "date"]
        )
    ]
)
class ExchangeRateEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "base_currency", nullable = false, length = 10)
    var baseCurrency: String,

    @Column(name = "quote_currency", nullable = false, length = 10)
    var quoteCurrency: String,

    @Column(name = "rate", nullable = false, precision = 20, scale = 10)
    var rate: BigDecimal,

    @Column(name = "date", nullable = false)
    var date: LocalDate,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()
) {

    /**
     * JPA 엔티티를 도메인 객체로 변환한다.
     */
    fun toDomain(): ExchangeRate = ExchangeRate(
        base = Currency.of(baseCurrency),
        quote = Currency.of(quoteCurrency),
        rate = rate,
        date = date
    )

    companion object {
        /**
         * 도메인 객체로부터 JPA 엔티티를 생성한다.
         */
        fun fromDomain(domain: ExchangeRate): ExchangeRateEntity = ExchangeRateEntity(
            baseCurrency = domain.base.code,
            quoteCurrency = domain.quote.code,
            rate = domain.rate,
            date = domain.date
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ExchangeRateEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "ExchangeRateEntity($baseCurrency/$quoteCurrency = $rate, date=$date)"
}
