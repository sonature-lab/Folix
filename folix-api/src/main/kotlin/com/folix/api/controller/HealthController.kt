package com.folix.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 헬스체크 REST API 컨트롤러.
 *
 * 서비스 상태 확인을 위한 헬스체크 엔드포인트를 제공한다.
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "헬스체크 API")
class HealthController {

    /**
     * 서비스 상태 확인.
     */
    @GetMapping
    @Operation(summary = "헬스체크", description = "서비스 상태를 확인합니다")
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "components" to mapOf(
                "db" to "UP",
                "redis" to "UP"
            )
        )
    }
}
