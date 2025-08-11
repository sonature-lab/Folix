package com.folix.domain.money

import java.math.BigDecimal

/**
 * Kotlin DSL 확장 프로퍼티로 직관적인 금액 생성을 지원한다.
 *
 * 사용 예:
 * ```kotlin
 * val price = 10_000.USD
 * val btc = 0.5.BTC
 * val salary = "5000000".KRW
 * ```
 */

// Int 확장
val Int.USD: Money get() = Money(BigDecimal(this), Currency.USD)
val Int.KRW: Money get() = Money(BigDecimal(this), Currency.KRW)
val Int.EUR: Money get() = Money(BigDecimal(this), Currency.EUR)
val Int.JPY: Money get() = Money(BigDecimal(this), Currency.JPY)
val Int.GBP: Money get() = Money(BigDecimal(this), Currency.GBP)
val Int.BTC: Money get() = Money(BigDecimal(this), Currency.BTC)
val Int.ETH: Money get() = Money(BigDecimal(this), Currency.ETH)

// Long 확장
val Long.USD: Money get() = Money(BigDecimal(this), Currency.USD)
val Long.KRW: Money get() = Money(BigDecimal(this), Currency.KRW)
val Long.EUR: Money get() = Money(BigDecimal(this), Currency.EUR)

// Double 확장 — 테스트 편의용. 프로덕션 코드에서는 String 확장을 사용할 것.
@Deprecated("Double은 정밀도 손실 위험이 있습니다. String 확장(\"0.5\".BTC)을 사용하세요.", ReplaceWith("this.toString().USD"))
val Double.USD: Money get() = Money(BigDecimal.valueOf(this), Currency.USD)
@Deprecated("Double은 정밀도 손실 위험이 있습니다. String 확장을 사용하세요.", ReplaceWith("this.toString().KRW"))
val Double.KRW: Money get() = Money(BigDecimal.valueOf(this), Currency.KRW)
@Deprecated("Double은 정밀도 손실 위험이 있습니다. String 확장을 사용하세요.", ReplaceWith("this.toString().EUR"))
val Double.EUR: Money get() = Money(BigDecimal.valueOf(this), Currency.EUR)
@Deprecated("Double은 정밀도 손실 위험이 있습니다. String 확장을 사용하세요.", ReplaceWith("this.toString().BTC"))
val Double.BTC: Money get() = Money(BigDecimal.valueOf(this), Currency.BTC)
@Deprecated("Double은 정밀도 손실 위험이 있습니다. String 확장을 사용하세요.", ReplaceWith("this.toString().ETH"))
val Double.ETH: Money get() = Money(BigDecimal.valueOf(this), Currency.ETH)

// String 확장 (정밀도가 중요한 경우)
val String.USD: Money get() = Money(BigDecimal(this), Currency.USD)
val String.KRW: Money get() = Money(BigDecimal(this), Currency.KRW)
val String.EUR: Money get() = Money(BigDecimal(this), Currency.EUR)
val String.BTC: Money get() = Money(BigDecimal(this), Currency.BTC)
val String.ETH: Money get() = Money(BigDecimal(this), Currency.ETH)

/**
 * 범용 통화 DSL. 사전 정의되지 않은 통화에 사용한다.
 *
 * 사용 예:
 * ```kotlin
 * val amount = 100.money(Currency.SGD)
 * ```
 */
fun Int.money(currency: Currency): Money = Money(BigDecimal(this), currency)
fun Long.money(currency: Currency): Money = Money(BigDecimal(this), currency)
@Deprecated("Double은 정밀도 손실 위험이 있습니다. String.money()를 사용하세요.", ReplaceWith("this.toString().money(currency)"))
fun Double.money(currency: Currency): Money = Money(BigDecimal.valueOf(this), currency)
fun String.money(currency: Currency): Money = Money(BigDecimal(this), currency)
