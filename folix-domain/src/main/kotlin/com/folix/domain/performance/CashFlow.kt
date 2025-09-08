package com.folix.domain.performance

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 현금 흐름을 나타내는 값 객체.
 *
 * 성과 계산(TWR/IRR)에 사용되는 현금 유출입 기록이다.
 * 양수는 유입(투자), 음수는 유출(회수)을 나타낸다.
 *
 * @property date 현금 흐름 발생일
 * @property amount 금액 (양수: 유입, 음수: 유출)
 */
data class CashFlow(
    val date: LocalDate,
    val amount: BigDecimal
) : Comparable<CashFlow> {

    override fun compareTo(other: CashFlow): Int = date.compareTo(other.date)

    companion object {
        /**
         * 유입(투자) 현금 흐름을 생성한다.
         */
        fun inflow(date: LocalDate, amount: BigDecimal): CashFlow {
            require(amount > BigDecimal.ZERO) { "Inflow amount must be positive: $amount" }
            return CashFlow(date, amount)
        }

        /**
         * 유출(회수) 현금 흐름을 생성한다.
         */
        fun outflow(date: LocalDate, amount: BigDecimal): CashFlow {
            require(amount > BigDecimal.ZERO) { "Outflow amount must be positive: $amount" }
            return CashFlow(date, amount.negate())
        }
    }
}
