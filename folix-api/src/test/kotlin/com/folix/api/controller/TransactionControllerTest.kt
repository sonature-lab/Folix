package com.folix.api.controller

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.folix.api.common.GlobalExceptionHandler
import com.folix.api.dto.CreateTransactionRequest
import com.folix.application.exception.EntityNotFoundException
import com.folix.application.port.`in`.TransactionUseCase
import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import com.folix.domain.asset.Asset
import com.folix.domain.money.Currency
import com.folix.domain.transaction.Transaction
import com.folix.domain.transaction.TransactionType
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class TransactionControllerTest : DescribeSpec({

    val transactionUseCase = mockk<TransactionUseCase>()
    val controller = TransactionController(transactionUseCase)
    val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(GlobalExceptionHandler())
        .build()
    val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    val account = Account(name = "Test", type = AccountType.BROKERAGE, currency = Currency.USD)
    val asset = Asset.stock("AAPL", "Apple Inc.")
    val transaction = Transaction(
        accountId = account.id, assetId = asset.id,
        type = TransactionType.BUY, quantity = BigDecimal("10"),
        price = BigDecimal("150.00"), currency = Currency.USD,
        fee = BigDecimal("9.99"), date = LocalDate.of(2026, 1, 15)
    )

    describe("POST /api/v1/transactions") {
        it("should_create_transaction") {
            val request = CreateTransactionRequest(
                accountId = account.id.toString(), assetId = asset.id.toString(),
                type = TransactionType.BUY, quantity = "10", price = "150.00",
                currency = "USD", fee = "9.99", date = LocalDate.of(2026, 1, 15)
            )
            every { transactionUseCase.createTransaction(any()) } returns transaction

            mockMvc.post("/api/v1/transactions") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data.type") { value("BUY") }
                jsonPath("$.data.quantity") { value("10") }
                jsonPath("$.data.price") { value("150.00") }
            }
        }
    }

    describe("GET /api/v1/transactions") {
        it("should_return_all_transactions") {
            every { transactionUseCase.getAllTransactions() } returns listOf(transaction)

            mockMvc.get("/api/v1/transactions")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.length()") { value(1) }
                    jsonPath("$.data[0].type") { value("BUY") }
                }
        }

        it("should_filter_by_account") {
            every { transactionUseCase.getTransactionsByAccount(account.id) } returns listOf(transaction)

            mockMvc.get("/api/v1/transactions") {
                param("accountId", account.id.toString())
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(1) }
            }
        }
    }

    describe("GET /api/v1/transactions/{id}") {
        it("should_return_transaction_by_id") {
            every { transactionUseCase.getTransaction(transaction.id) } returns transaction

            mockMvc.get("/api/v1/transactions/${transaction.id}")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.type") { value("BUY") }
                }
        }

        it("should_return_404_when_not_found") {
            val badId = UUID.randomUUID()
            every { transactionUseCase.getTransaction(badId) } throws
                EntityNotFoundException("Transaction", badId)

            mockMvc.get("/api/v1/transactions/$badId")
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.error.code") { value("RES_001") }
                }
        }
    }
})
