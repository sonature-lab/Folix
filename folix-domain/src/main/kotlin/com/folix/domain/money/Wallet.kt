package com.folix.domain.money

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 다통화 보유를 나타내는 값 객체.
 *
 * 여러 통화의 [Money]를 보유하고, 특정 기준 통화로 합산할 수 있다.
 *
 * @property holdings 통화별 보유 금액
 */
data class Wallet(
    private val holdings: Map<Currency, Money> = emptyMap()
) {

    /** 보유 통화 목록 */
    val currencies: Set<Currency> get() = holdings.keys

    /** 보유 통화 수 */
    val size: Int get() = holdings.size

    /** 비어있는지 확인 */
    val isEmpty: Boolean get() = holdings.isEmpty()

    /**
     * 특정 통화의 보유 금액을 반환한다.
     *
     * @param currency 조회할 통화
     * @return 보유 금액 (없으면 0)
     */
    operator fun get(currency: Currency): Money =
        holdings[currency] ?: Money.zero(currency)

    /**
     * 금액을 추가한다.
     *
     * @param money 추가할 금액
     * @return 새로운 [Wallet]
     */
    fun deposit(money: Money): Wallet {
        val current = this[money.currency]
        return Wallet(holdings + (money.currency to current + money))
    }

    /**
     * 금액을 차감한다.
     *
     * @param money 차감할 금액
     * @return 새로운 [Wallet]
     */
    fun withdraw(money: Money): Wallet {
        val current = this[money.currency]
        return Wallet(holdings + (money.currency to current - money))
    }

    /**
     * 모든 보유 금액을 기준 통화로 변환하여 합산한다.
     *
     * @param targetCurrency 기준 통화
     * @param rateProvider 환율 조회 함수
     * @return 기준 통화로 합산된 총 금액
     */
    fun totalIn(
        targetCurrency: Currency,
        rateProvider: (Currency, Currency) -> ExchangeRate
    ): Money {
        if (holdings.isEmpty()) return Money.zero(targetCurrency)

        val total = holdings.values.fold(BigDecimal.ZERO) { acc, money ->
            if (money.currency == targetCurrency) {
                acc + money.amount
            } else {
                val rate = rateProvider(money.currency, targetCurrency)
                val converted = rate.convert(money)
                acc + converted.amount
            }
        }

        return Money(
            total.setScale(Money.DEFAULT_SCALE, RoundingMode.HALF_UP),
            targetCurrency
        )
    }

    /** 모든 보유 금액 목록을 반환한다. */
    fun toList(): List<Money> = holdings.values.toList()

    override fun toString(): String =
        if (isEmpty) "Wallet(empty)"
        else "Wallet(${holdings.values.joinToString(", ")})"

    companion object {
        /** 빈 지갑을 생성한다. */
        fun empty(): Wallet = Wallet()

        /** 단일 금액으로 지갑을 생성한다. */
        fun of(vararg monies: Money): Wallet {
            var wallet = empty()
            monies.forEach { wallet = wallet.deposit(it) }
            return wallet
        }
    }
}
