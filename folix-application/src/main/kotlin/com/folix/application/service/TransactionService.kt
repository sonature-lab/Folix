package com.folix.application.service

import com.folix.application.exception.EntityNotFoundException
import com.folix.application.port.`in`.TransactionUseCase
import com.folix.application.port.`in`.TransactionUseCase.CreateTransactionCommand
import com.folix.application.port.out.AccountRepository
import com.folix.application.port.out.AssetRepository
import com.folix.application.port.out.TransactionRepository
import com.folix.domain.money.Currency
import com.folix.domain.transaction.Transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

/**
 * 거래 관리 유즈케이스 구현.
 */
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val assetRepository: AssetRepository
) : TransactionUseCase {

    override fun createTransaction(command: CreateTransactionCommand): Transaction {
        accountRepository.findById(command.accountId)
            ?: throw EntityNotFoundException("Account", command.accountId)
        assetRepository.findById(command.assetId)
            ?: throw EntityNotFoundException("Asset", command.assetId)

        val transaction = Transaction(
            accountId = command.accountId,
            assetId = command.assetId,
            type = command.type,
            quantity = BigDecimal(command.quantity),
            price = BigDecimal(command.price),
            fee = command.fee?.let { BigDecimal(it) } ?: BigDecimal.ZERO,
            currency = Currency.of(command.currency),
            date = command.date,
            note = command.note
        )

        return transactionRepository.save(transaction)
    }

    override fun getTransaction(id: UUID): Transaction =
        transactionRepository.findById(id)
            ?: throw EntityNotFoundException("Transaction", id)

    override fun getAllTransactions(): List<Transaction> =
        transactionRepository.findAll()

    override fun getTransactionsByAccount(accountId: UUID): List<Transaction> =
        transactionRepository.findByAccountId(accountId)

    override fun getTransactionsByAsset(assetId: UUID): List<Transaction> =
        transactionRepository.findByAssetId(assetId)

    override fun getTransactionsByDateRange(from: LocalDate, to: LocalDate): List<Transaction> =
        transactionRepository.findByDateRange(from, to)
}
