package com.folix.application.port.`in`

import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import java.util.UUID

/**
 * 계좌 관리 인바운드 포트.
 */
interface AccountUseCase {

    data class CreateAccountCommand(
        val name: String,
        val type: AccountType,
        val currency: String,
        val institution: String? = null
    )

    fun createAccount(command: CreateAccountCommand): Account
    fun getAccount(id: UUID): Account
    fun getAllAccounts(): List<Account>
}
