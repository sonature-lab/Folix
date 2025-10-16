package com.folix.api.dto

import com.folix.domain.account.Account
import com.folix.domain.account.AccountType

/**
 * 계좌 생성 요청 DTO.
 */
data class CreateAccountRequest(
    val name: String,
    val type: AccountType,
    val currency: String,
    val institution: String? = null
)

/**
 * 계좌 응답 DTO.
 */
data class AccountResponse(
    val id: String,
    val name: String,
    val type: String,
    val currency: String,
    val institution: String?
) {
    companion object {
        /**
         * 도메인 Account를 AccountResponse로 변환.
         */
        fun from(account: Account): AccountResponse = AccountResponse(
            id = account.id.toString(),
            name = account.name,
            type = account.type.name,
            currency = account.currency.code,
            institution = account.institution
        )
    }
}
