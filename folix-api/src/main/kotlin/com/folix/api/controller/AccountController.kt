package com.folix.api.controller

import com.folix.api.common.ApiResponse
import com.folix.api.dto.AccountResponse
import com.folix.api.dto.CreateAccountRequest
import com.folix.application.port.`in`.AccountUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * 계좌 관리 REST API 컨트롤러.
 *
 * 계좌 등록, 조회 기능을 제공한다.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "계좌 관리 API")
class AccountController(
    private val accountUseCase: AccountUseCase
) {

    /**
     * 계좌 등록.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "계좌 등록", description = "새로운 계좌를 등록합니다")
    fun createAccount(@RequestBody request: CreateAccountRequest): ApiResponse<AccountResponse> {
        val command = AccountUseCase.CreateAccountCommand(
            name = request.name,
            type = request.type,
            currency = request.currency,
            institution = request.institution
        )
        val account = accountUseCase.createAccount(command)
        return ApiResponse.ok(AccountResponse.from(account))
    }

    /**
     * 계좌 목록 조회.
     */
    @GetMapping
    @Operation(summary = "계좌 목록 조회", description = "전체 계좌 목록을 조회합니다")
    fun getAllAccounts(): ApiResponse<List<AccountResponse>> {
        val accounts = accountUseCase.getAllAccounts()
        return ApiResponse.ok(accounts.map { AccountResponse.from(it) })
    }

    /**
     * 계좌 단건 조회.
     */
    @GetMapping("/{id}")
    @Operation(summary = "계좌 조회", description = "ID로 특정 계좌를 조회합니다")
    fun getAccount(@PathVariable id: String): ApiResponse<AccountResponse> {
        val account = accountUseCase.getAccount(UUID.fromString(id))
        return ApiResponse.ok(AccountResponse.from(account))
    }
}
