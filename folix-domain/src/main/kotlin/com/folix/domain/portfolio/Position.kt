package com.folix.domain.portfolio

import com.folix.domain.money.Currency
import com.folix.domain.money.Money
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

/**
 * 보유 포지션을 나타내는 값 객체.
 *
 * 특정 자산의 보유 수량, 평균 매입단가, 현재가를 기반으로
 * 평가금액, 미실현 손익, 수익률을 계산한다.
 *
 * @property assetId 자산 ID
 * @property symbol 종목 코드
 * @property name 자산명
 * @property quantity 보유 수량
 * @property averageCost 평균 매입단가
 * @property currentPrice 현재가
 * @property currency 통화
 */
data class Position(
    val assetId: UUID,
    val symbol: String,
    val name: String,
    val quantity: BigDecimal,
    val averageCost: BigDecimal,
    val currentPrice: BigDecimal,
    val currency: Currency
) {

    init {
        require(quantity >= BigDecimal.ZERO) { "Quantity must be non-negative: $quantity" }
        require(averageCost >= BigDecimal.ZERO) { "Average cost must be non-negative: $averageCost" }
        require(currentPrice >= BigDecimal.ZERO) { "Current price must be non-negative: $currentPrice" }
    }

    /** 총 투자금액 (수량 x 평균단가) */
    val totalCost: BigDecimal
        get() = quantity.multiply(averageCost)
            .setScale(Money.DEFAULT_SCALE, RoundingMode.HALF_UP)

    /** 현재 평가금액 (수량 x 현재가) */
    val marketValue: BigDecimal
        get() = quantity.multiply(currentPrice)
            .setScale(Money.DEFAULT_SCALE, RoundingMode.HALF_UP)

    /** 미실현 손익 (평가금액 - 투자금액) */
    val unrealizedPnl: BigDecimal
        get() = marketValue - totalCost

    /**
     * 수익률 (%).
     * totalCost가 0이면 BigDecimal.ZERO를 반환한다.
     */
    val returnRate: BigDecimal
        get() = if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal.ZERO
        } else {
            unrealizedPnl.divide(totalCost, RATE_SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
        }

    /** 평가금액을 [Money]로 반환한다. */
    fun marketValueAsMoney(): Money = Money(marketValue, currency)

    /** 미실현 손익을 [Money]로 반환한다. */
    fun unrealizedPnlAsMoney(): Money = Money(unrealizedPnl, currency)

    /** 보유 수량이 0인지 확인한다. */
    val isClosed: Boolean
        get() = quantity.compareTo(BigDecimal.ZERO) == 0

    override fun toString(): String = "$symbol: $quantity @ $currentPrice $currency"

    companion object {
        private const val RATE_SCALE = 4
    }
}
