package com.folix.api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.folix.api.common.GlobalExceptionHandler
import com.folix.api.dto.CreateAccountRequest
import com.folix.application.exception.EntityNotFoundException
import com.folix.application.port.`in`.AccountUseCase
import com.folix.domain.account.Account
import com.folix.domain.account.AccountType
import com.folix.domain.money.Currency
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.UUID

class AccountControllerTest : DescribeSpec({

    val accountUseCase = mockk<AccountUseCase>()
    val controller = AccountController(accountUseCase)
    val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(GlobalExceptionHandler())
        .build()
    val objectMapper = jacksonObjectMapper()

    val account = Account(
        name = "Binance", type = AccountType.EXCHANGE,
        currency = Currency.USD, institution = "Binance"
    )

    describe("POST /api/v1/accounts") {
        it("should_create_account_and_return_201") {
            val request = CreateAccountRequest(
                name = "Binance", type = AccountType.EXCHANGE,
                currency = "USD", institution = "Binance"
            )
            every { accountUseCase.createAccount(any()) } returns account

            mockMvc.post("/api/v1/accounts") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data.name") { value("Binance") }
                jsonPath("$.data.type") { value("EXCHANGE") }
            }
        }
    }

    describe("GET /api/v1/accounts") {
        it("should_return_all_accounts") {
            every { accountUseCase.getAllAccounts() } returns listOf(account)

            mockMvc.get("/api/v1/accounts")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.length()") { value(1) }
                    jsonPath("$.data[0].name") { value("Binance") }
                }
        }
    }

    describe("GET /api/v1/accounts/{id}") {
        it("should_return_account_by_id") {
            every { accountUseCase.getAccount(account.id) } returns account

            mockMvc.get("/api/v1/accounts/${account.id}")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.name") { value("Binance") }
                }
        }

        it("should_return_404_when_not_found") {
            val badId = UUID.randomUUID()
            every { accountUseCase.getAccount(badId) } throws
                EntityNotFoundException("Account", badId)

            mockMvc.get("/api/v1/accounts/$badId")
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.success") { value(false) }
                    jsonPath("$.error.code") { value("RES_001") }
                }
        }
    }
})
