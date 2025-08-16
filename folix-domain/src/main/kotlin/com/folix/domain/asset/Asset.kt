package com.folix.domain.asset

import com.folix.domain.money.Currency
import java.util.UUID

/**
 * 투자 자산을 나타내는 엔티티.
 *
 * 주식, 암호화폐, 채권, 펀드 등 다양한 자산 유형을 표현한다.
 *
 * @property id 자산 고유 ID
 * @property symbol 종목 코드 (예: AAPL, BTC)
 * @property name 자산명 (예: "Apple Inc.")
 * @property type 자산 유형
 * @property assetClass 자산 분류군
 * @property currency 기준 통화
 * @property exchange 거래소 (예: NASDAQ, Binance). nullable
 */
data class Asset(
    val id: UUID = UUID.randomUUID(),
    val symbol: String,
    val name: String,
    val type: AssetType,
    val assetClass: AssetClass,
    val currency: Currency,
    val exchange: String? = null
) {

    init {
        require(symbol.isNotBlank()) { "Asset symbol must not be blank" }
        require(name.isNotBlank()) { "Asset name must not be blank" }
    }

    override fun toString(): String = "$symbol ($name)"

    companion object {
        /**
         * 주식 자산을 생성하는 편의 팩토리.
         */
        fun stock(
            symbol: String,
            name: String,
            currency: Currency = Currency.USD,
            exchange: String? = null
        ): Asset = Asset(
            symbol = symbol,
            name = name,
            type = AssetType.STOCK,
            assetClass = AssetClass.EQUITY,
            currency = currency,
            exchange = exchange
        )

        /**
         * 암호화폐 자산을 생성하는 편의 팩토리.
         */
        fun crypto(
            symbol: String,
            name: String,
            currency: Currency = Currency.USD
        ): Asset = Asset(
            symbol = symbol,
            name = name,
            type = AssetType.CRYPTO,
            assetClass = AssetClass.CRYPTOCURRENCY,
            currency = currency
        )

        /**
         * 현금/예금 자산을 생성하는 편의 팩토리.
         */
        fun cash(currency: Currency): Asset = Asset(
            symbol = currency.code,
            name = "${currency.code} Cash",
            type = AssetType.CASH,
            assetClass = AssetClass.CASH_EQUIVALENT,
            currency = currency
        )
    }
}
