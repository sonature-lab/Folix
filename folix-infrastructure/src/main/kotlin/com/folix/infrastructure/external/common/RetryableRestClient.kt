package com.folix.infrastructure.external.common

import org.slf4j.LoggerFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

/**
 * 재시도 로직을 포함한 RestClient 래퍼.
 *
 * 일시적 오류(429, 5xx)에 대해 지수 백오프로 재시도한다.
 */
class RetryableRestClient(
    private val delegate: RestClient,
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 1000,
    private val backoffFactor: Double = 2.0
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun <T> execute(
        operation: String = "HTTP request",
        request: RestClient.() -> T
    ): T {
        var lastException: Exception? = null
        var attempt = 0

        while (attempt <= maxRetries) {
            try {
                return request(delegate)
            } catch (e: RestClientResponseException) {
                lastException = e

                if (!isRetryableStatus(e.statusCode.value()) || attempt == maxRetries) {
                    logger.error("$operation failed after ${attempt + 1} attempts", e)
                    throw e
                }

                val delayMs = calculateDelay(attempt)
                logger.warn(
                    "$operation failed (attempt ${attempt + 1}/$maxRetries): ${e.statusCode} - " +
                            "Retrying in ${delayMs}ms"
                )
                Thread.sleep(delayMs)
                attempt++
            } catch (e: Exception) {
                logger.error("$operation failed with non-retryable error", e)
                throw e
            }
        }

        throw lastException ?: IllegalStateException("Retry logic failed without exception")
    }

    private fun isRetryableStatus(statusCode: Int): Boolean =
        statusCode == 429 || statusCode in 500..504

    private fun calculateDelay(attempt: Int): Long =
        (baseDelayMs * Math.pow(backoffFactor, attempt.toDouble())).toLong()
}

/**
 * RestClient를 RetryableRestClient로 래핑하는 확장 함수.
 */
fun RestClient.withRetry(
    maxRetries: Int = 3,
    baseDelayMs: Long = 1000,
    backoffFactor: Double = 2.0
): RetryableRestClient = RetryableRestClient(this, maxRetries, baseDelayMs, backoffFactor)
