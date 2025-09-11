package com.folix.domain.performance

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.temporal.ChronoUnit

/**
 * 내부수익률(IRR) 계산기.
 *
 * IRR은 투자의 순현재가치(NPV)를 0으로 만드는 할인율이다.
 * Newton-Raphson 반복법으로 근사한다.
 *
 * NPV = Σ CF_i / (1 + r)^(t_i / 365)
 * IRR = NPV가 0이 되는 r
 */
object InternalRateOfReturn {

    private const val MAX_ITERATIONS = 1000
    private const val DEFAULT_SCALE = 8
    private val TOLERANCE = BigDecimal("0.0000001")
    private val INITIAL_GUESS = BigDecimal("0.1")

    /**
     * IRR을 계산한다.
     *
     * 현금 흐름 목록과 최종 가치로 IRR을 구한다.
     * 마지막 현금 흐름은 최종 포트폴리오 가치(유출)를 나타낸다.
     *
     * @param cashFlows 현금 흐름 목록 (최소 투자 + 최종 가치 포함)
     * @return IRR (소수, 예: 0.15 = 15%), null이면 수렴 실패
     */
    fun calculate(cashFlows: List<CashFlow>): BigDecimal? {
        require(cashFlows.size >= 2) { "At least 2 cash flows required (investment + final value)" }

        val sorted = cashFlows.sorted()
        val baseDate = sorted.first().date

        var rate = INITIAL_GUESS

        for (iteration in 0 until MAX_ITERATIONS) {
            val npv = computeNpv(sorted, baseDate, rate)
            val derivative = computeNpvDerivative(sorted, baseDate, rate)

            if (derivative.abs() < TOLERANCE) break

            val adjustment = npv.divide(derivative, DEFAULT_SCALE + 4, RoundingMode.HALF_UP)
            rate = rate - adjustment

            if (npv.abs() < TOLERANCE) {
                return rate.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP)
            }
        }

        // 마지막 NPV 확인 — 충분히 작으면 수렴으로 간주
        val finalNpv = computeNpv(sorted, baseDate, rate)
        return if (finalNpv.abs() < BigDecimal("0.01")) {
            rate.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP)
        } else {
            null
        }
    }

    /**
     * NPV를 계산한다.
     *
     * NPV = Σ CF_i / (1 + r)^(t_i / 365)
     */
    private fun computeNpv(
        cashFlows: List<CashFlow>,
        baseDate: java.time.LocalDate,
        rate: BigDecimal
    ): BigDecimal {
        val onePlusRate = BigDecimal.ONE + rate
        return cashFlows.fold(BigDecimal.ZERO) { acc, cf ->
            val years = yearFraction(baseDate, cf.date)
            val discountFactor = Math.pow(onePlusRate.toDouble(), years)
            val pv = cf.amount.divide(
                BigDecimal(discountFactor),
                DEFAULT_SCALE + 4,
                RoundingMode.HALF_UP
            )
            acc + pv
        }
    }

    /**
     * NPV의 도함수를 계산한다 (Newton-Raphson용).
     *
     * dNPV/dr = Σ -t_i * CF_i / (1 + r)^(t_i + 1)
     */
    private fun computeNpvDerivative(
        cashFlows: List<CashFlow>,
        baseDate: java.time.LocalDate,
        rate: BigDecimal
    ): BigDecimal {
        val onePlusRate = BigDecimal.ONE + rate
        return cashFlows.fold(BigDecimal.ZERO) { acc, cf ->
            val years = yearFraction(baseDate, cf.date)
            val discountFactor = Math.pow(onePlusRate.toDouble(), years + 1.0)
            val derivative = cf.amount.multiply(BigDecimal(-years))
                .divide(BigDecimal(discountFactor), DEFAULT_SCALE + 4, RoundingMode.HALF_UP)
            acc + derivative
        }
    }

    private fun yearFraction(from: java.time.LocalDate, to: java.time.LocalDate): Double {
        val days = ChronoUnit.DAYS.between(from, to)
        return days.toDouble() / 365.0
    }
}
