package com.folix.infrastructure.external.common

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import java.time.Duration

/**
 * 외부 API 클라이언트 설정.
 */
@Configuration
@EnableConfigurationProperties(ExternalApiProperties::class)
class RestClientConfig {

    @Bean
    fun yahooRestClient(properties: ExternalApiProperties): RetryableRestClient {
        val client = RestClient.builder()
            .baseUrl(properties.yahoo.baseUrl)
            .defaultHeader("User-Agent", "Folix/1.0")
            .build()
        return client.withRetry()
    }

    @Bean
    fun coingeckoRestClient(properties: ExternalApiProperties): RetryableRestClient {
        val builder = RestClient.builder()
            .baseUrl(properties.coingecko.baseUrl)
            .defaultHeader("User-Agent", "Folix/1.0")

        properties.coingecko.apiKey?.let { apiKey ->
            builder.defaultHeader("x-cg-demo-api-key", apiKey)
        }

        return builder.build().withRetry()
    }

    @Bean
    fun ecbRestClient(properties: ExternalApiProperties): RetryableRestClient {
        val client = RestClient.builder()
            .baseUrl(properties.ecb.baseUrl)
            .defaultHeader("User-Agent", "Folix/1.0")
            .build()
        return client.withRetry()
    }
}
