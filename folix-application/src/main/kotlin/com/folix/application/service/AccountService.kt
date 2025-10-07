package com.folix.application.service

import com.folix.application.exception.EntityNotFoundException
import com.folix.application.port.`in`.AccountUseCase
import com.folix.application.port.`in`.AccountUseCase.CreateAccountCommand
import com.folix.application.port.out.AccountRepository
import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import com.folix.domain.money.Currency
import java.util.UUID

/**
 * 계좌 관리 유즈케이스 구현.
 */
class AccountService(
    private val accountRepository: AccountRepository
) : AccountUseCase {

    override fun createAccount(command: CreateAccountCommand): Account {
        val account = Account(
            name = command.name,
            type = command.type,
            currency = Currency.of(command.currency),
            institution = command.institution
        )
        return accountRepository.save(account)
    }

    override fun getAccount(id: UUID): Account =
        accountRepository.findById(id)
            ?: throw EntityNotFoundException("Account", id)

    override fun getAllAccounts(): List<Account> =
        accountRepository.findAll()
}
