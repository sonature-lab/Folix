package com.folix.infrastructure.persistence.adapter

import com.folix.application.port.out.AccountRepository
import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import com.folix.infrastructure.persistence.entity.AccountEntity
import com.folix.infrastructure.persistence.repository.JpaAccountRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*

/**
 * [AccountRepository] 인터페이스 구현체.
 *
 * JPA를 통해 Account 도메인 객체의 영속성을 담당한다.
 */
@Repository
class AccountRepositoryAdapter(
    private val jpaRepository: JpaAccountRepository
) : AccountRepository {

    override fun save(account: Account): Account {
        val entity = AccountEntity.fromDomain(account)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: UUID): Account? {
        return jpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findAll(): List<Account> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findByType(type: AccountType): List<Account> {
        return jpaRepository.findByType(type).map { it.toDomain() }
    }

    override fun deleteById(id: UUID) {
        jpaRepository.deleteById(id)
    }
}
