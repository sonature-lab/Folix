package com.folix.domain.portfolio

import com.folix.domain.money.Currency
import com.folix.domain.money.ExchangeRate
import com.folix.domain.money.Money
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 포트폴리오를 나타내는 집합 객체.
 *
 * 보유 포지션을 관리하고, 총 자산가치/수익률/배분 등을 계산한다.
 *
 * @property positions 보유 포지션 목록
 * @property baseCurrency 기준 통화
 */
data class Portfolio(
    val positions: List<Position>,
    val baseCurrency: Currency = Currency.USD
) {

    /** 활성 포지션 (수량 > 0) */
    val activePositions: List<Position>
        get() = positions.filter { !it.isClosed }

    /** 보유 종목 수 */
    val positionCount: Int
        get() = activePositions.size

    /**
     * 총 평가금액.
     *
     * baseCurrency와 동일한 통화의 포지션만 합산한다.
     * 멀티통화 포트폴리오에서 모든 통화를 환산하여 합산하려면 [totalValueIn]을 사용한다.
     */
    val totalMarketValue: BigDecimal
        get() = activePositions
            .filter { it.currency == baseCurrency }
            .sumOf { it.marketValue }

    /**
     * 총 투자금액 (기준 통화 기준).
     */
    val totalCost: BigDecimal
        get() = activePositions
            .filter { it.currency == baseCurrency }
            .sumOf { it.totalCost }

    /**
     * 총 미실현 손익.
     */
    val totalUnrealizedPnl: BigDecimal
        get() = totalMarketValue - totalCost

    /**
     * 총 수익률 (%).
     */
    val totalReturnRate: BigDecimal
        get() = if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal.ZERO
        } else {
            totalUnrealizedPnl.divide(totalCost, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
        }

    /**
     * 멀티통화 총 평가금액을 기준 통화로 합산한다.
     *
     * @param rateProvider 환율 조회 함수
     * @return 기준 통화로 합산된 총 평가금액
     */
    fun totalValueIn(
        targetCurrency: Currency = baseCurrency,
        rateProvider: (Currency, Currency) -> ExchangeRate
    ): Money {
        val total = activePositions.fold(BigDecimal.ZERO) { acc, position ->
            val value = if (position.currency == targetCurrency) {
                position.marketValue
            } else {
                val rate = rateProvider(position.currency, targetCurrency)
                rate.convert(position.marketValueAsMoney()).amount
            }
            acc + value
        }
        return Money(total.setScale(Money.DEFAULT_SCALE, RoundingMode.HALF_UP), targetCurrency)
    }

    /**
     * 자산 유형별 배분을 계산한다.
     */
    fun allocationByType(): List<Allocation> =
        Allocation.fromPositions(activePositions) { it.symbol }

    /**
     * 통화별 배분을 계산한다.
     */
    fun allocationByCurrency(): List<Allocation> =
        Allocation.fromPositions(activePositions) { it.currency.code }

    override fun toString(): String =
        "Portfolio($positionCount positions, ${baseCurrency.code})"
}
