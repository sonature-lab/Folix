package com.folix.api.controller

import com.folix.api.common.ApiResponse
import com.folix.api.dto.CreateTransactionRequest
import com.folix.api.dto.TransactionResponse
import com.folix.application.port.`in`.TransactionUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

/**
 * 거래 관리 REST API 컨트롤러.
 *
 * 거래 기록, 조회 기능을 제공한다.
 */
@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "거래 관리 API")
class TransactionController(
    private val transactionUseCase: TransactionUseCase
) {

    /**
     * 거래 기록.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "거래 기록", description = "새로운 거래를 기록합니다")
    fun createTransaction(@RequestBody request: CreateTransactionRequest): ApiResponse<TransactionResponse> {
        val command = TransactionUseCase.CreateTransactionCommand(
            accountId = UUID.fromString(request.accountId),
            assetId = UUID.fromString(request.assetId),
            type = request.type,
            quantity = request.quantity,
            price = request.price,
            currency = request.currency,
            fee = request.fee,
            date = request.date,
            note = request.note
        )
        val transaction = transactionUseCase.createTransaction(command)
        return ApiResponse.ok(TransactionResponse.from(transaction))
    }

    /**
     * 거래 목록 조회.
     */
    @GetMapping
    @Operation(summary = "거래 목록 조회", description = "거래 내역을 조회합니다. 계좌/자산/기간 필터 가능")
    fun getAllTransactions(
        @RequestParam(required = false) accountId: String?,
        @RequestParam(required = false) assetId: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?
    ): ApiResponse<List<TransactionResponse>> {
        val transactions = when {
            accountId != null -> transactionUseCase.getTransactionsByAccount(UUID.fromString(accountId))
            assetId != null -> transactionUseCase.getTransactionsByAsset(UUID.fromString(assetId))
            from != null && to != null -> transactionUseCase.getTransactionsByDateRange(from, to)
            else -> transactionUseCase.getAllTransactions()
        }
        return ApiResponse.ok(transactions.map { TransactionResponse.from(it) })
    }

    /**
     * 거래 단건 조회.
     */
    @GetMapping("/{id}")
    @Operation(summary = "거래 조회", description = "ID로 특정 거래를 조회합니다")
    fun getTransaction(@PathVariable id: String): ApiResponse<TransactionResponse> {
        val transaction = transactionUseCase.getTransaction(UUID.fromString(id))
        return ApiResponse.ok(TransactionResponse.from(transaction))
    }
}
