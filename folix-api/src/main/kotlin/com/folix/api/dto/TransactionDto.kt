package com.folix.api.dto

import com.folix.domain.transaction.Transaction
import com.folix.domain.transaction.TransactionType
import java.time.LocalDate

/**
 * 거래 생성 요청 DTO.
 */
data class CreateTransactionRequest(
    val accountId: String,
    val assetId: String,
    val type: TransactionType,
    val quantity: String,
    val price: String,
    val currency: String,
    val fee: String? = null,
    val date: LocalDate,
    val note: String? = null
)

/**
 * 거래 응답 DTO.
 */
data class TransactionResponse(
    val id: String,
    val accountId: String,
    val assetId: String,
    val type: String,
    val quantity: String,
    val price: String,
    val totalAmount: String,
    val fee: String,
    val currency: String,
    val date: LocalDate,
    val note: String?
) {
    companion object {
        /**
         * 도메인 Transaction을 TransactionResponse로 변환.
         */
        fun from(transaction: Transaction): TransactionResponse = TransactionResponse(
            id = transaction.id.toString(),
            accountId = transaction.accountId.toString(),
            assetId = transaction.assetId.toString(),
            type = transaction.type.name,
            quantity = transaction.quantity.toPlainString(),
            price = transaction.price.toPlainString(),
            totalAmount = transaction.totalAmount.toPlainString(),
            fee = transaction.fee.toPlainString(),
            currency = transaction.currency.code,
            date = transaction.date,
            note = transaction.note
        )
    }
}
