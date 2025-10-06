package com.folix.application.port.out

import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import java.util.UUID

/**
 * 계좌 영속화를 위한 아웃바운드 포트.
 */
interface AccountRepository {
    fun save(account: Account): Account
    fun findById(id: UUID): Account?
    fun findAll(): List<Account>
    fun findByType(type: AccountType): List<Account>
    fun deleteById(id: UUID)
}
