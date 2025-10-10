package com.folix.application.service

import com.folix.application.exception.EntityNotFoundException
import com.folix.application.port.`in`.AccountUseCase.CreateAccountCommand
import com.folix.application.port.out.AccountRepository
import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import com.folix.domain.money.Currency
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class AccountServiceTest : DescribeSpec({

    val accountRepository = mockk<AccountRepository>()
    val accountService = AccountService(accountRepository)

    val account = Account(
        name = "Binance",
        type = AccountType.EXCHANGE,
        currency = Currency.USD,
        institution = "Binance"
    )

    describe("createAccount") {
        it("should_create_account") {
            val command = CreateAccountCommand(
                name = "Binance", type = AccountType.EXCHANGE,
                currency = "USD", institution = "Binance"
            )
            every { accountRepository.save(any()) } answers { firstArg() }

            val result = accountService.createAccount(command)
            result.name shouldBe "Binance"
            result.type shouldBe AccountType.EXCHANGE
            result.currency shouldBe Currency.USD
        }
    }

    describe("getAccount") {
        it("should_return_account_when_found") {
            every { accountRepository.findById(account.id) } returns account

            val result = accountService.getAccount(account.id)
            result.name shouldBe "Binance"
        }

        it("should_throw_when_not_found") {
            val id = UUID.randomUUID()
            every { accountRepository.findById(id) } returns null

            shouldThrow<EntityNotFoundException> {
                accountService.getAccount(id)
            }
        }
    }

    describe("getAllAccounts") {
        it("should_return_all_accounts") {
            every { accountRepository.findAll() } returns listOf(account)

            val result = accountService.getAllAccounts()
            result.size shouldBe 1
        }
    }
})
