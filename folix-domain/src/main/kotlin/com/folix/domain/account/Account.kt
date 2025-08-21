package com.folix.domain.account

import com.folix.domain.money.Currency
import java.util.UUID

/**
 * 투자 계좌를 나타내는 엔티티.
 *
 * 증권 계좌, 은행 계좌, 크립토 거래소 등 투자 수단이 귀속되는 계좌를 표현한다.
 *
 * @property id 계좌 고유 ID
 * @property name 계좌명 (예: "키움증권", "Binance")
 * @property type 계좌 유형
 * @property currency 기준 통화
 * @property institution 금융기관명. nullable
 */
data class Account(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val type: AccountType,
    val currency: Currency,
    val institution: String? = null
) {

    init {
        require(name.isNotBlank()) { "Account name must not be blank" }
    }

    override fun toString(): String = "$name (${type.name})"

    companion object {
        /**
         * 증권 계좌를 생성하는 편의 팩토리.
         */
        fun brokerage(
            name: String,
            currency: Currency,
            institution: String? = null
        ): Account = Account(
            name = name,
            type = AccountType.BROKERAGE,
            currency = currency,
            institution = institution
        )

        /**
         * 크립토 거래소 계좌를 생성하는 편의 팩토리.
         */
        fun exchange(
            name: String,
            currency: Currency = Currency.USD
        ): Account = Account(
            name = name,
            type = AccountType.EXCHANGE,
            currency = currency,
            institution = name
        )
    }
}
