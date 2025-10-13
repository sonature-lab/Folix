package com.folix.infrastructure.persistence.entity

import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import com.folix.domain.money.Currency
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

/**
 * Account JPA 엔티티.
 *
 * 도메인 [Account]와 분리된 영속성 모델.
 */
@Entity
@Table(
    name = "accounts",
    indexes = [
        Index(name = "idx_account_type", columnList = "type")
    ]
)
class AccountEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false, length = 200)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    var type: AccountType,

    @Column(name = "currency", nullable = false, length = 10)
    var currency: String,

    @Column(name = "institution", length = 200)
    var institution: String? = null,

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
    fun toDomain(): Account = Account(
        id = id,
        name = name,
        type = type,
        currency = Currency.of(currency),
        institution = institution
    )

    companion object {
        /**
         * 도메인 객체로부터 JPA 엔티티를 생성한다.
         */
        fun fromDomain(domain: Account): AccountEntity = AccountEntity(
            id = domain.id,
            name = domain.name,
            type = domain.type,
            currency = domain.currency.code,
            institution = domain.institution
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AccountEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "AccountEntity(id=$id, name='$name', type=$type)"
}
