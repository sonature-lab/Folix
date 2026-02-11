package com.folix.infrastructure.cache

import com.folix.application.port.out.ExchangeRateProvider
import com.folix.domain.money.Currency
import com.folix.domain.money.ExchangeRate
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
import java.time.LocalDate

class CachedExchangeRateProviderTest : DescribeSpec({

    describe("fetchLatestRate") {
        it("should return cached value when cache hit") {
            val delegate = mockk<ExchangeRateProvider>()
            val redisTemplate = mockk<RedisTemplate<String, String>>()
            val valueOps = mockk<ValueOperations<String, String>>()

            every { redisTemplate.opsForValue() } returns valueOps
            every { valueOps.get("rate:USD:KRW") } returns "1350.5000000000|2026-02-11"

            val provider = CachedExchangeRateProvider(
                delegate = delegate,
                redisTemplate = redisTemplate,
                latestRateTtlMinutes = 60
            )

            val result = provider.fetchLatestRate("USD", "KRW")

            result.base shouldBe Currency.of("USD")
            result.quote shouldBe Currency.of("KRW")
            result.rate shouldBe BigDecimal("1350.5000000000")
            result.date shouldBe LocalDate.of(2026, 2, 11)

            verify(exactly = 0) { delegate.fetchLatestRate(any(), any()) }
        }

        it("should call delegate when cache miss and save to cache") {
            val delegate = mockk<ExchangeRateProvider>()
            val redisTemplate = mockk<RedisTemplate<String, String>>()
            val valueOps = mockk<ValueOperations<String, String>>()

            val rate = ExchangeRate(
                base = Currency.of("USD"),
                quote = Currency.of("KRW"),
                rate = BigDecimal("1350.5000000000"),
                date = LocalDate.of(2026, 2, 11)
            )

            val keySlot = slot<String>()
            val valueSlot = slot<String>()
            val ttlSlot = slot<Duration>()

            every { redisTemplate.opsForValue() } returns valueOps
            every { valueOps.get("rate:USD:KRW") } returns null
            every { valueOps.set(capture(keySlot), capture(valueSlot), capture(ttlSlot)) } returns Unit
            every { delegate.fetchLatestRate("USD", "KRW") } returns rate
            every { delegate.sourceName() } returns "TestProvider"

            val provider = CachedExchangeRateProvider(
                delegate = delegate,
                redisTemplate = redisTemplate,
                latestRateTtlMinutes = 60
            )

            val result = provider.fetchLatestRate("USD", "KRW")

            result shouldBe rate

            verify(exactly = 1) { delegate.fetchLatestRate("USD", "KRW") }
            verify(exactly = 1) { valueOps.set(any<String>(), any<String>(), any<Duration>()) }

            keySlot.captured shouldBe "rate:USD:KRW"
            valueSlot.captured shouldBe "1350.5000000000|2026-02-11"
        }
    }
})
