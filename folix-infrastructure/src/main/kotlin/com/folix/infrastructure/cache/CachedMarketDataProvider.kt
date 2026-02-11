package com.folix.infrastructure.cache

import com.folix.application.port.out.MarketDataProvider
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate

/**
 * Redis 캐싱을 적용한 MarketDataProvider 데코레이터.
 *
 * 현재 가격만 캐싱하고, 히스토리컬 데이터는 캐싱하지 않는다.
 */
class CachedMarketDataProvider(
    private val delegate: MarketDataProvider,
    private val redisTemplate: RedisTemplate<String, String>,
    private val ttlMinutes: Long
) : MarketDataProvider by delegate {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun fetchCurrentPrice(symbol: String): BigDecimal {
        val cacheKey = buildCacheKey(symbol)

        return try {
            // 캐시 조회
            redisTemplate.opsForValue().get(cacheKey)?.let { cached ->
                logger.debug("Cache hit for price: $symbol")
                return BigDecimal(cached)
            }

            // 캐시 미스 - delegate 호출
            logger.debug("Cache miss for price: $symbol")
            val price = delegate.fetchCurrentPrice(symbol)

            // 캐시 저장
            redisTemplate.opsForValue().set(
                cacheKey,
                price.toPlainString(),
                Duration.ofMinutes(ttlMinutes)
            )

            price
        } catch (e: Exception) {
            logger.warn("Redis error, falling back to delegate: ${e.message}")
            delegate.fetchCurrentPrice(symbol)
        }
    }

    override fun fetchHistoricalPrices(
        symbol: String,
        from: LocalDate,
        to: LocalDate
    ): List<Pair<LocalDate, BigDecimal>> {
        // 히스토리컬 데이터는 캐싱하지 않음
        return delegate.fetchHistoricalPrices(symbol, from, to)
    }

    private fun buildCacheKey(symbol: String): String =
        "price:${delegate.sourceName()}:$symbol"
}
