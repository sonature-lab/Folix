package com.folix.infrastructure.persistence.repository

import com.folix.domain.account.AccountType
import com.folix.infrastructure.persistence.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Account용 Spring Data JPA 리포지토리.
 */
@Repository
interface JpaAccountRepository : JpaRepository<AccountEntity, UUID> {

    /**
     * 계좌 유형으로 조회한다.
     */
    fun findByType(type: AccountType): List<AccountEntity>
}
