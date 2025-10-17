package com.folix.api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI (Swagger) 설정.
 *
 * API 문서화를 위한 OpenAPI 3.0 설정을 제공한다.
 * Swagger UI는 /swagger-ui 경로에서 확인 가능하다.
 */
@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Folix API")
                .version("1.0")
                .description(
                    """
                    Folix 개인 자산 모델링 엔진 REST API

                    - 멀티통화 포트폴리오 관리
                    - 성과 계산 (TWR/IRR)
                    - 리스크 분석
                    - 몬테카를로 시뮬레이션

                    모든 금액은 BigDecimal 정밀도를 유지하기 위해 문자열로 전달됩니다.
                    """.trimIndent()
                )
        )
}
