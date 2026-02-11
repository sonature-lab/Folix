package com.folix.infrastructure.config

import com.folix.application.port.out.ExchangeRateProvider
import com.folix.application.port.out.MarketDataProvider
import com.folix.infrastructure.cache.CachedExchangeRateProvider
import com.folix.infrastructure.cache.CachedMarketDataProvider
import com.folix.infrastructure.external.coingecko.CoinGeckoClient
import com.folix.infrastructure.external.common.ExternalApiProperties
import com.folix.infrastructure.external.ecb.EcbExchangeRateClient
import com.folix.infrastructure.external.yahoo.YahooFinanceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.RedisTemplate

/**
 * 외부 API 클라이언트 빈 설정.
 *
 * 캐싱이 적용된 Provider를 빈으로 등록한다.
 */
@Configuration
class ExternalApiConfig {

    @Bean
    fun cachedYahooProvider(
        yahooClient: YahooFinanceClient,
        redisTemplate: RedisTemplate<String, String>,
        properties: ExternalApiProperties
    ): MarketDataProvider {
        return CachedMarketDataProvider(
            delegate = yahooClient,
            redisTemplate = redisTemplate,
            ttlMinutes = properties.cache.priceTtlMinutes
        )
    }

    @Bean
    fun cachedCoinGeckoProvider(
        coinGeckoClient: CoinGeckoClient,
        redisTemplate: RedisTemplate<String, String>,
        properties: ExternalApiProperties
    ): MarketDataProvider {
        return CachedMarketDataProvider(
            delegate = coinGeckoClient,
            redisTemplate = redisTemplate,
            ttlMinutes = properties.cache.priceTtlMinutes
        )
    }

    @Bean
    @Primary
    fun cachedExchangeRateProvider(
        ecbClient: EcbExchangeRateClient,
        redisTemplate: RedisTemplate<String, String>,
        properties: ExternalApiProperties
    ): ExchangeRateProvider {
        return CachedExchangeRateProvider(
            delegate = ecbClient,
            redisTemplate = redisTemplate,
            latestRateTtlMinutes = properties.cache.rateTtlMinutes
        )
    }
}
