package com.folix.application.port.`in`

import com.folix.domain.transaction.Transaction
import com.folix.domain.transaction.TransactionType
import java.time.LocalDate
import java.util.UUID

/**
 * 거래 관리 인바운드 포트.
 */
interface TransactionUseCase {

    data class CreateTransactionCommand(
        val accountId: UUID,
        val assetId: UUID,
        val type: TransactionType,
        val quantity: String,
        val price: String,
        val currency: String,
        val fee: String? = null,
        val date: LocalDate,
        val note: String? = null
    )

    data class ImportResult(
        val imported: Int,
        val failed: Int,
        val errors: List<String>
    )

    fun createTransaction(command: CreateTransactionCommand): Transaction
    fun getTransaction(id: UUID): Transaction
    fun getAllTransactions(): List<Transaction>
    fun getTransactionsByAccount(accountId: UUID): List<Transaction>
    fun getTransactionsByAsset(assetId: UUID): List<Transaction>
    fun getTransactionsByDateRange(from: LocalDate, to: LocalDate): List<Transaction>
}
