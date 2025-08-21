package com.folix.domain.transaction

import com.folix.domain.money.Currency
import com.folix.domain.money.Money
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

/**
 * 거래 내역을 나타내는 엔티티.
 *
 * 매수, 매도, 배당, 이자, 입금, 출금 등 투자 거래를 기록한다.
 *
 * @property id 거래 고유 ID
 * @property accountId 계좌 ID
 * @property assetId 자산 ID
 * @property type 거래 유형
 * @property quantity 수량
 * @property price 단가
 * @property fee 수수료
 * @property currency 거래 통화
 * @property date 거래일
 * @property note 메모
 */
data class Transaction(
    val id: UUID = UUID.randomUUID(),
    val accountId: UUID,
    val assetId: UUID,
    val type: TransactionType,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val fee: BigDecimal = BigDecimal.ZERO,
    val currency: Currency,
    val date: LocalDate,
    val note: String? = null
) {

    init {
        require(quantity > BigDecimal.ZERO) { "Quantity must be positive: $quantity" }
        require(price >= BigDecimal.ZERO) { "Price must be non-negative: $price" }
        require(fee >= BigDecimal.ZERO) { "Fee must be non-negative: $fee" }
    }

    /**
     * 거래 총액 (수량 x 단가).
     */
    val totalAmount: BigDecimal
        get() = quantity.multiply(price)
            .setScale(Money.DEFAULT_SCALE, RoundingMode.HALF_UP)

    /**
     * 수수료 포함 총 비용.
     * 매수 시: totalAmount + fee
     * 매도 시: totalAmount - fee
     */
    val netAmount: BigDecimal
        get() = when {
            type.isInflow -> totalAmount + fee
            type.isOutflow -> totalAmount - fee
            else -> totalAmount
        }

    /**
     * 거래 총액을 [Money]로 반환한다.
     */
    fun totalAsMoney(): Money = Money(totalAmount, currency)

    /**
     * 수수료를 [Money]로 반환한다.
     */
    fun feeAsMoney(): Money = Money(fee, currency)

    override fun toString(): String =
        "$type $quantity @ $price $currency ($date)"
}
