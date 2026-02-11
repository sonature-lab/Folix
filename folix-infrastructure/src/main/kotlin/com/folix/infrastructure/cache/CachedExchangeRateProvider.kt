package com.folix.infrastructure.cache

import com.folix.application.port.out.ExchangeRateProvider
import com.folix.domain.money.Currency
import com.folix.domain.money.ExchangeRate
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate

/**
 * Redis 캐싱을 적용한 ExchangeRateProvider 데코레이터.
 *
 * 직렬화 형식: "{rate}|{date}" (예: "1350.5000000000|2026-02-11")
 */
class CachedExchangeRateProvider(
    private val delegate: ExchangeRateProvider,
    private val redisTemplate: RedisTemplate<String, String>,
    private val latestRateTtlMinutes: Long,
    private val historicalRateTtlDays: Long = 7
) : ExchangeRateProvider by delegate {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun fetchLatestRate(baseCurrency: String, quoteCurrency: String): ExchangeRate {
        val cacheKey = buildLatestRateKey(baseCurrency, quoteCurrency)

        return try {
            // 캐시 조회
            redisTemplate.opsForValue().get(cacheKey)?.let { cached ->
                logger.debug("Cache hit for latest rate: $baseCurrency/$quoteCurrency")
                return deserializeExchangeRate(cached, baseCurrency, quoteCurrency)
            }

            // 캐시 미스 - delegate 호출
            logger.debug("Cache miss for latest rate: $baseCurrency/$quoteCurrency")
            val rate = delegate.fetchLatestRate(baseCurrency, quoteCurrency)

            // 캐시 저장
            redisTemplate.opsForValue().set(
                cacheKey,
                serializeExchangeRate(rate),
                Duration.ofMinutes(latestRateTtlMinutes)
            )

            rate
        } catch (e: Exception) {
            logger.warn("Redis error, falling back to delegate: ${e.message}")
            delegate.fetchLatestRate(baseCurrency, quoteCurrency)
        }
    }

    override fun fetchRateAt(
        baseCurrency: String,
        quoteCurrency: String,
        date: LocalDate
    ): ExchangeRate {
        val cacheKey = buildHistoricalRateKey(baseCurrency, quoteCurrency, date)

        return try {
            // 캐시 조회
            redisTemplate.opsForValue().get(cacheKey)?.let { cached ->
                logger.debug("Cache hit for historical rate: $baseCurrency/$quoteCurrency at $date")
                return deserializeExchangeRate(cached, baseCurrency, quoteCurrency)
            }

            // 캐시 미스 - delegate 호출
            logger.debug("Cache miss for historical rate: $baseCurrency/$quoteCurrency at $date")
            val rate = delegate.fetchRateAt(baseCurrency, quoteCurrency, date)

            // 히스토리컬 데이터는 불변이므로 더 긴 TTL 사용
            redisTemplate.opsForValue().set(
                cacheKey,
                serializeExchangeRate(rate),
                Duration.ofDays(historicalRateTtlDays)
            )

            rate
        } catch (e: Exception) {
            logger.warn("Redis error, falling back to delegate: ${e.message}")
            delegate.fetchRateAt(baseCurrency, quoteCurrency, date)
        }
    }

    private fun buildLatestRateKey(base: String, quote: String): String =
        "rate:$base:$quote"

    private fun buildHistoricalRateKey(base: String, quote: String, date: LocalDate): String =
        "rate:$base:$quote:$date"

    private fun serializeExchangeRate(rate: ExchangeRate): String =
        "${rate.rate.toPlainString()}|${rate.date}"

    private fun deserializeExchangeRate(
        serialized: String,
        baseCurrency: String,
        quoteCurrency: String
    ): ExchangeRate {
        val parts = serialized.split("|")
        require(parts.size == 2) { "Invalid serialized ExchangeRate: $serialized" }

        val rate = BigDecimal(parts[0])
        val date = LocalDate.parse(parts[1])

        return ExchangeRate(
            base = Currency.of(baseCurrency),
            quote = Currency.of(quoteCurrency),
            rate = rate,
            date = date
        )
    }
}
