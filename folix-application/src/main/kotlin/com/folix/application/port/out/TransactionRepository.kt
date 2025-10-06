package com.folix.application.port.out

import com.folix.domain.transaction.Transaction
import com.folix.domain.transaction.TransactionType
import java.time.LocalDate
import java.util.UUID

/**
 * 거래 영속화를 위한 아웃바운드 포트.
 */
interface TransactionRepository {
    fun save(transaction: Transaction): Transaction
    fun saveAll(transactions: List<Transaction>): List<Transaction>
    fun findById(id: UUID): Transaction?
    fun findAll(): List<Transaction>
    fun findByAccountId(accountId: UUID): List<Transaction>
    fun findByAssetId(assetId: UUID): List<Transaction>
    fun findByDateRange(from: LocalDate, to: LocalDate): List<Transaction>
    fun findByAccountIdAndDateRange(accountId: UUID, from: LocalDate, to: LocalDate): List<Transaction>
    fun deleteById(id: UUID)
}
