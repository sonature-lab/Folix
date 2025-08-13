package com.folix.domain.money

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * 환율을 나타내는 값 객체.
 *
 * [base] 통화 1단위를 [quote] 통화로 변환하는 비율을 표현한다.
 * 예: ExchangeRate(USD, KRW, 1350.00) → 1 USD = 1,350 KRW
 *
 * @property base 기준 통화
 * @property quote 대상 통화
 * @property rate 환율 (base 1단위당 quote 금액)
 * @property date 환율 기준 날짜
 */
data class ExchangeRate(
    val base: Currency,
    val quote: Currency,
    val rate: BigDecimal,
    val date: LocalDate = LocalDate.now()
) {

    init {
        require(base != quote) { "Base and quote currency must differ: $base" }
        require(rate > BigDecimal.ZERO) { "Exchange rate must be positive: $rate" }
    }

    /**
     * [Money]를 [quote] 통화로 변환한다.
     *
     * @param money 변환할 금액 ([base] 통화여야 함)
     * @return [quote] 통화로 변환된 금액
     * @throws IllegalArgumentException 입력 금액의 통화가 [base]와 일치하지 않을 때
     */
    fun convert(money: Money): Money {
        require(money.currency == base) {
            "Cannot convert ${money.currency} with $base/$quote rate"
        }
        val converted = money.amount.multiply(rate)
            .setScale(Money.DEFAULT_SCALE, RoundingMode.HALF_UP)
        return Money(converted, quote)
    }

    /**
     * 역환율을 반환한다.
     *
     * 예: USD/KRW 1350 → KRW/USD 0.00074074
     */
    fun invert(): ExchangeRate = ExchangeRate(
        base = quote,
        quote = base,
        rate = BigDecimal.ONE.divide(rate, RATE_SCALE, RoundingMode.HALF_UP),
        date = date
    )

    override fun toString(): String = "$base/$quote = $rate ($date)"

    companion object {
        /** 환율 소수점 자릿수 */
        const val RATE_SCALE = 10
    }
}
