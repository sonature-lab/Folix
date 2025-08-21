package com.folix.domain.transaction

/**
 * 거래 유형을 나타내는 열거형.
 */
enum class TransactionType {
    BUY,
    SELL,
    DIVIDEND,
    INTEREST,
    DEPOSIT,
    WITHDRAWAL;

    /** 이 거래가 자산 수량을 증가시키는지 여부 */
    val isInflow: Boolean
        get() = this == BUY || this == DEPOSIT

    /** 이 거래가 자산 수량을 감소시키는지 여부 */
    val isOutflow: Boolean
        get() = this == SELL || this == WITHDRAWAL

    /** 이 거래가 수익(배당/이자)인지 여부 */
    val isIncome: Boolean
        get() = this == DIVIDEND || this == INTEREST
}
