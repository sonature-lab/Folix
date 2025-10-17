package com.folix.api.common

import com.folix.application.exception.DuplicateEntityException
import com.folix.application.exception.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 핸들러.
 *
 * 애플리케이션 전역에서 발생하는 예외를 HTTP 응답으로 변환한다.
 * 에러 코드는 error-codes.md SSOT 문서를 따른다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 엔티티를 찾을 수 없을 때 발생하는 예외 처리.
     * HTTP 404 Not Found 반환.
     */
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Entity not found: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("RES_001", ex.message ?: "Resource not found"))
    }

    /**
     * 중복 엔티티 예외 처리.
     * HTTP 409 Conflict 반환.
     */
    @ExceptionHandler(DuplicateEntityException::class)
    fun handleDuplicateEntity(ex: DuplicateEntityException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Duplicate entity: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("RES_002", ex.message ?: "Resource already exists"))
    }

    /**
     * 잘못된 요청 파라미터 예외 처리.
     * HTTP 400 Bad Request 반환.
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Invalid argument: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("VAL_001", ex.message ?: "Invalid request parameters"))
    }

    /**
     * 예상하지 못한 서버 오류 처리.
     * HTTP 500 Internal Server Error 반환.
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unexpected error occurred", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("SYS_001", "Internal server error"))
    }
}
