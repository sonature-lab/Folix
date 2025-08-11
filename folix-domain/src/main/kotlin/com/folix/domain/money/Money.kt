package com.folix.domain.money

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 금액을 나타내는 값 객체.
 *
 * 모든 금융 계산은 [BigDecimal]을 사용하며, [Double]/[Float]은 절대 사용하지 않는다.
 * 동일 통화 간에만 산술 연산이 가능하며, 통화 불일치 시 즉시 실패한다.
 *
 * @property amount 금액 (BigDecimal)
 * @property currency 통화
 */
data class Money(
    val amount: BigDecimal,
    val currency: Currency
) : Comparable<Money> {

    init {
        require(amount.scale() <= MAX_SCALE) {
            "Amount scale exceeds maximum ($MAX_SCALE): ${amount.scale()}"
        }
    }

    /** 금액이 0인지 확인 */
    val isZero: Boolean get() = amount.compareTo(BigDecimal.ZERO) == 0

    /** 금액이 양수인지 확인 */
    val isPositive: Boolean get() = amount > BigDecimal.ZERO

    /** 금액이 음수인지 확인 */
    val isNegative: Boolean get() = amount < BigDecimal.ZERO

    /**
     * 동일 통화의 [Money]를 더한다.
     *
     * @throws IllegalArgumentException 통화가 일치하지 않을 때
     */
    operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return Money(amount + other.amount, currency)
    }

    /**
     * 동일 통화의 [Money]를 뺀다.
     *
     * @throws IllegalArgumentException 통화가 일치하지 않을 때
     */
    operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return Money(amount - other.amount, currency)
    }

    /**
     * 스칼라 값을 곱한다.
     */
    operator fun times(multiplier: BigDecimal): Money =
        Money(amount.multiply(multiplier), currency)

    /**
     * 정수 스칼라 값을 곱한다.
     */
    operator fun times(multiplier: Int): Money =
        times(BigDecimal(multiplier))

    /**
     * 스칼라 값으로 나눈다.
     *
     * @param divisor 나눌 값
     * @param scale 소수점 자릿수 (기본: [DEFAULT_SCALE])
     * @param roundingMode 반올림 모드 (기본: [RoundingMode.HALF_UP])
     */
    fun divide(
        divisor: BigDecimal,
        scale: Int = DEFAULT_SCALE,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): Money {
        require(divisor.compareTo(BigDecimal.ZERO) != 0) { "Cannot divide by zero" }
        return Money(amount.divide(divisor, scale, roundingMode), currency)
    }

    /** 부호를 반전한다. */
    operator fun unaryMinus(): Money = Money(amount.negate(), currency)

    /** 절대값을 반환한다. */
    fun abs(): Money = Money(amount.abs(), currency)

    override fun compareTo(other: Money): Int {
        requireSameCurrency(other)
        return amount.compareTo(other.amount)
    }

    override fun toString(): String = "$amount $currency"

    private fun requireSameCurrency(other: Money) {
        require(currency == other.currency) {
            "Currency mismatch: cannot operate on $currency and ${other.currency}"
        }
    }

    companion object {
        /** 금융 계산 기본 소수점 자릿수 */
        const val DEFAULT_SCALE = 8

        /** 최대 허용 소수점 자릿수 */
        const val MAX_SCALE = 20

        /**
         * 특정 통화의 0 금액을 생성한다.
         */
        fun zero(currency: Currency): Money = Money(BigDecimal.ZERO, currency)

        /**
         * 문자열 금액과 통화로 [Money]를 생성한다.
         *
         * @param amount 금액 문자열 (예: "10000.50")
         * @param currency 통화
         */
        fun of(amount: String, currency: Currency): Money =
            Money(BigDecimal(amount), currency)

        /**
         * [BigDecimal] 금액과 통화로 [Money]를 생성한다.
         */
        fun of(amount: BigDecimal, currency: Currency): Money =
            Money(amount, currency)
    }
}
