package com.folix.application.service

import com.folix.application.exception.EntityNotFoundException
import com.folix.application.port.`in`.TransactionUseCase.CreateTransactionCommand
import com.folix.application.port.out.AccountRepository
import com.folix.application.port.out.AssetRepository
import com.folix.application.port.out.TransactionRepository
import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import com.folix.domain.asset.Asset
import com.folix.domain.money.Currency
import com.folix.domain.transaction.TransactionType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.util.UUID

class TransactionServiceTest : DescribeSpec({

    val transactionRepository = mockk<TransactionRepository>()
    val accountRepository = mockk<AccountRepository>()
    val assetRepository = mockk<AssetRepository>()
    val transactionService = TransactionService(transactionRepository, accountRepository, assetRepository)

    val account = Account(name = "Test", type = AccountType.BROKERAGE, currency = Currency.USD)
    val asset = Asset.stock("AAPL", "Apple Inc.")

    describe("createTransaction") {
        it("should_create_transaction_when_account_and_asset_exist") {
            val command = CreateTransactionCommand(
                accountId = account.id, assetId = asset.id,
                type = TransactionType.BUY, quantity = "10", price = "150.00",
                currency = "USD", fee = "9.99", date = LocalDate.of(2026, 1, 15)
            )
            every { accountRepository.findById(account.id) } returns account
            every { assetRepository.findById(asset.id) } returns asset
            every { transactionRepository.save(any()) } answers { firstArg() }

            val result = transactionService.createTransaction(command)
            result.type shouldBe TransactionType.BUY
            result.quantity.toPlainString() shouldBe "10"
            result.price.toPlainString() shouldBe "150.00"
            result.fee.toPlainString() shouldBe "9.99"
        }

        it("should_throw_when_account_not_found") {
            val badAccountId = UUID.randomUUID()
            val command = CreateTransactionCommand(
                accountId = badAccountId, assetId = asset.id,
                type = TransactionType.BUY, quantity = "10", price = "100",
                currency = "USD", date = LocalDate.now()
            )
            every { accountRepository.findById(badAccountId) } returns null

            shouldThrow<EntityNotFoundException> {
                transactionService.createTransaction(command)
            }
        }

        it("should_throw_when_asset_not_found") {
            val badAssetId = UUID.randomUUID()
            val command = CreateTransactionCommand(
                accountId = account.id, assetId = badAssetId,
                type = TransactionType.BUY, quantity = "10", price = "100",
                currency = "USD", date = LocalDate.now()
            )
            every { accountRepository.findById(account.id) } returns account
            every { assetRepository.findById(badAssetId) } returns null

            shouldThrow<EntityNotFoundException> {
                transactionService.createTransaction(command)
            }
        }

        it("should_default_fee_to_zero_when_not_provided") {
            val command = CreateTransactionCommand(
                accountId = account.id, assetId = asset.id,
                type = TransactionType.BUY, quantity = "5", price = "200",
                currency = "USD", date = LocalDate.now()
            )
            every { accountRepository.findById(account.id) } returns account
            every { assetRepository.findById(asset.id) } returns asset
            every { transactionRepository.save(any()) } answers { firstArg() }

            val result = transactionService.createTransaction(command)
            result.fee.compareTo(java.math.BigDecimal.ZERO) shouldBe 0
        }
    }

    describe("getTransaction") {
        it("should_throw_when_not_found") {
            val id = UUID.randomUUID()
            every { transactionRepository.findById(id) } returns null

            shouldThrow<EntityNotFoundException> {
                transactionService.getTransaction(id)
            }
        }
    }
})
