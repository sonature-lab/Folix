package com.folix.api.common

/**
 * 공통 API 응답 래퍼.
 *
 * 모든 API 응답은 이 형식으로 반환된다.
 * 성공 시 data에 결과를, 실패 시 error에 에러 정보를 담는다.
 *
 * @param T 응답 데이터 타입
 * @property success 성공 여부
 * @property data 응답 데이터 (성공 시)
 * @property error 에러 정보 (실패 시)
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ErrorDetail?
) {
    /**
     * 에러 상세 정보.
     *
     * @property code 에러 코드 (예: RES_001)
     * @property message 사용자 친화적 에러 메시지
     */
    data class ErrorDetail(
        val code: String,
        val message: String
    )

    companion object {
        /**
         * 성공 응답 생성.
         */
        fun <T> ok(data: T): ApiResponse<T> = ApiResponse(
            success = true,
            data = data,
            error = null
        )

        /**
         * 에러 응답 생성.
         */
        fun error(code: String, message: String): ApiResponse<Nothing> = ApiResponse(
            success = false,
            data = null,
            error = ErrorDetail(code, message)
        )
    }
}
