package com.folix.infrastructure.persistence.entity

import com.folix.domain.money.Currency
import com.folix.domain.transaction.Transaction
import com.folix.domain.transaction.TransactionType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.*

/**
 * Transaction JPA 엔티티.
 *
 * 도메인 [Transaction]과 분리된 영속성 모델.
 * 모든 금액은 NUMERIC(20,8)로 저장된다.
 */
@Entity
@Table(
    name = "transactions",
    indexes = [
        Index(name = "idx_transaction_account", columnList = "account_id"),
        Index(name = "idx_transaction_asset", columnList = "asset_id"),
        Index(name = "idx_transaction_date", columnList = "date"),
        Index(name = "idx_transaction_account_date", columnList = "account_id,date")
    ]
)
class TransactionEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "account_id", nullable = false)
    var accountId: UUID,

    @Column(name = "asset_id", nullable = false)
    var assetId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    var type: TransactionType,

    @Column(name = "quantity", nullable = false, precision = 20, scale = 8)
    var quantity: BigDecimal,

    @Column(name = "price", nullable = false, precision = 20, scale = 8)
    var price: BigDecimal,

    @Column(name = "fee", nullable = false, precision = 20, scale = 8)
    var fee: BigDecimal = BigDecimal.ZERO,

    @Column(name = "currency", nullable = false, length = 10)
    var currency: String,

    @Column(name = "date", nullable = false)
    var date: LocalDate,

    @Column(name = "note", length = 500)
    var note: String? = null,

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
    fun toDomain(): Transaction = Transaction(
        id = id,
        accountId = accountId,
        assetId = assetId,
        type = type,
        quantity = quantity,
        price = price,
        fee = fee,
        currency = Currency.of(currency),
        date = date,
        note = note
    )

    companion object {
        /**
         * 도메인 객체로부터 JPA 엔티티를 생성한다.
         */
        fun fromDomain(domain: Transaction): TransactionEntity = TransactionEntity(
            id = domain.id,
            accountId = domain.accountId,
            assetId = domain.assetId,
            type = domain.type,
            quantity = domain.quantity,
            price = domain.price,
            fee = domain.fee,
            currency = domain.currency.code,
            date = domain.date,
            note = domain.note
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TransactionEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "TransactionEntity(id=$id, type=$type, date=$date)"
}
