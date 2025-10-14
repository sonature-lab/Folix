package com.folix.infrastructure.persistence.adapter

import com.folix.application.port.out.TransactionRepository
import com.folix.domain.transaction.Transaction
import com.folix.infrastructure.persistence.entity.TransactionEntity
import com.folix.infrastructure.persistence.repository.JpaTransactionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

/**
 * [TransactionRepository] 인터페이스 구현체.
 *
 * JPA를 통해 Transaction 도메인 객체의 영속성을 담당한다.
 */
@Repository
class TransactionRepositoryAdapter(
    private val jpaRepository: JpaTransactionRepository
) : TransactionRepository {

    override fun save(transaction: Transaction): Transaction {
        val entity = TransactionEntity.fromDomain(transaction)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun saveAll(transactions: List<Transaction>): List<Transaction> {
        val entities = transactions.map { TransactionEntity.fromDomain(it) }
        val saved = jpaRepository.saveAll(entities)
        return saved.map { it.toDomain() }
    }

    override fun findById(id: UUID): Transaction? {
        return jpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findAll(): List<Transaction> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findByAccountId(accountId: UUID): List<Transaction> {
        return jpaRepository.findByAccountId(accountId).map { it.toDomain() }
    }

    override fun findByAssetId(assetId: UUID): List<Transaction> {
        return jpaRepository.findByAssetId(assetId).map { it.toDomain() }
    }

    override fun findByDateRange(from: LocalDate, to: LocalDate): List<Transaction> {
        return jpaRepository.findByDateRange(from, to).map { it.toDomain() }
    }

    override fun findByAccountIdAndDateRange(
        accountId: UUID,
        from: LocalDate,
        to: LocalDate
    ): List<Transaction> {
        return jpaRepository.findByAccountIdAndDateRange(accountId, from, to)
            .map { it.toDomain() }
    }

    override fun deleteById(id: UUID) {
        jpaRepository.deleteById(id)
    }
}
