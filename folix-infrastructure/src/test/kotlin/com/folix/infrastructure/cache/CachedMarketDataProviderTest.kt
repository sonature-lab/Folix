package com.folix.infrastructure.cache

import com.folix.application.port.out.MarketDataProvider
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.math.BigDecimal
import java.time.Duration

class CachedMarketDataProviderTest : DescribeSpec({

    describe("fetchCurrentPrice") {
        it("should return cached value when cache hit") {
            val delegate = mockk<MarketDataProvider>()
            val redisTemplate = mockk<RedisTemplate<String, String>>()
            val valueOps = mockk<ValueOperations<String, String>>()

            every { delegate.sourceName() } returns "YahooFinance"
            every { delegate.supports(any()) } returns true
            every { redisTemplate.opsForValue() } returns valueOps
            every { valueOps.get("price:YahooFinance:AAPL") } returns "150.50000000"

            val provider = CachedMarketDataProvider(
                delegate = delegate,
                redisTemplate = redisTemplate,
                ttlMinutes = 5
            )

            val result = provider.fetchCurrentPrice("AAPL")

            result shouldBe BigDecimal("150.50000000")
            verify(exactly = 0) { delegate.fetchCurrentPrice(any()) }
        }

        it("should call delegate when cache miss and save to cache") {
            val delegate = mockk<MarketDataProvider>()
            val redisTemplate = mockk<RedisTemplate<String, String>>()
            val valueOps = mockk<ValueOperations<String, String>>()

            val keySlot = slot<String>()
            val valueSlot = slot<String>()
            val ttlSlot = slot<Duration>()

            every { delegate.sourceName() } returns "YahooFinance"
            every { delegate.supports(any()) } returns true
            every { redisTemplate.opsForValue() } returns valueOps
            every { valueOps.get("price:YahooFinance:AAPL") } returns null
            every { delegate.fetchCurrentPrice("AAPL") } returns BigDecimal("150.50000000")
            every { valueOps.set(capture(keySlot), capture(valueSlot), capture(ttlSlot)) } returns Unit

            val provider = CachedMarketDataProvider(
                delegate = delegate,
                redisTemplate = redisTemplate,
                ttlMinutes = 5
            )

            val result = provider.fetchCurrentPrice("AAPL")

            result shouldBe BigDecimal("150.50000000")
            keySlot.captured shouldBe "price:YahooFinance:AAPL"
            valueSlot.captured shouldBe "150.50000000"
            ttlSlot.captured shouldBe Duration.ofMinutes(5)
            verify(exactly = 1) { delegate.fetchCurrentPrice("AAPL") }
        }

        it("should fallback to delegate when redis error") {
            val delegate = mockk<MarketDataProvider>()
            val redisTemplate = mockk<RedisTemplate<String, String>>()
            val valueOps = mockk<ValueOperations<String, String>>()

            every { delegate.sourceName() } returns "YahooFinance"
            every { delegate.supports(any()) } returns true
            every { redisTemplate.opsForValue() } returns valueOps
            every { valueOps.get(any()) } throws RuntimeException("Redis connection refused")
            every { delegate.fetchCurrentPrice("AAPL") } returns BigDecimal("150.50000000")

            val provider = CachedMarketDataProvider(
                delegate = delegate,
                redisTemplate = redisTemplate,
                ttlMinutes = 5
            )

            val result = provider.fetchCurrentPrice("AAPL")

            result shouldBe BigDecimal("150.50000000")
            verify(exactly = 1) { delegate.fetchCurrentPrice("AAPL") }
        }
    }
})
