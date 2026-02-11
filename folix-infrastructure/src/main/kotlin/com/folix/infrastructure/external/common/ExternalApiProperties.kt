package com.folix.infrastructure.external.common

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 외부 API 연동 설정.
 *
 * application.yml에서 `folix.external.*` 프로퍼티를 바인딩한다.
 */
@ConfigurationProperties(prefix = "folix.external")
data class ExternalApiProperties(
    val yahoo: YahooProperties = YahooProperties(),
    val coingecko: CoinGeckoProperties = CoinGeckoProperties(),
    val ecb: EcbProperties = EcbProperties(),
    val cache: CacheProperties = CacheProperties()
) {

    data class YahooProperties(
        val baseUrl: String = "https://query1.finance.yahoo.com",
        val timeoutMs: Long = 10000
    )

    data class CoinGeckoProperties(
        val baseUrl: String = "https://api.coingecko.com/api/v3",
        val apiKey: String? = null,
        val timeoutMs: Long = 10000
    )

    data class EcbProperties(
        val baseUrl: String = "https://www.ecb.europa.eu/stats/eurofxref",
        val timeoutMs: Long = 10000
    )

    data class CacheProperties(
        val priceTtlMinutes: Long = 5,
        val rateTtlMinutes: Long = 60
    )
}
