package com.folix.infrastructure.external.coingecko

import com.folix.application.port.out.MarketDataProvider
import com.folix.infrastructure.external.common.RetryableRestClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * CoinGecko API를 통한 암호화폐 시세 제공자.
 */
@Component
class CoinGeckoClient(
    @Qualifier("coingeckoRestClient") private val restClient: RetryableRestClient
) : MarketDataProvider {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun fetchCurrentPrice(symbol: String): BigDecimal {
        logger.debug("Fetching current price for crypto symbol: $symbol")

        val coinId = symbolToCoinId(symbol)
        val response = restClient.execute("CoinGecko fetchCurrentPrice($symbol)") {
            get()
                .uri("/simple/price?ids={coinId}&vs_currencies=usd", coinId)
                .retrieve()
                .body(object : ParameterizedTypeReference<Map<String, Map<String, Double>>>() {})
                ?: throw IllegalStateException("Empty response from CoinGecko")
        }

        val price = response[coinId]?.get("usd")
            ?: throw IllegalStateException("No price data found for symbol: $symbol")

        return BigDecimal.valueOf(price).setScale(PRICE_SCALE, RoundingMode.HALF_UP)
    }

    override fun fetchHistoricalPrices(
        symbol: String,
        from: LocalDate,
        to: LocalDate
    ): List<Pair<LocalDate, BigDecimal>> {
        logger.debug("Fetching historical prices for $symbol from $from to $to")

        val coinId = symbolToCoinId(symbol)
        val fromTimestamp = from.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        val toTimestamp = to.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

        val response = restClient.execute("CoinGecko fetchHistoricalPrices($symbol)") {
            get()
                .uri("/coins/{coinId}/market_chart/range?vs_currency=usd&from={from}&to={to}",
                    coinId, fromTimestamp, toTimestamp)
                .retrieve()
                .body(CoinGeckoMarketChartResponse::class.java)
                ?: throw IllegalStateException("Empty response from CoinGecko")
        }

        return response.prices.map { priceData ->
            val timestampMs = priceData[0].toLong()
            val price = priceData[1].toDouble()

            val date = Instant.ofEpochMilli(timestampMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val amount = BigDecimal.valueOf(price).setScale(PRICE_SCALE, RoundingMode.HALF_UP)

            date to amount
        }
    }

    override fun supports(assetType: String): Boolean =
        assetType.uppercase() == "CRYPTO"

    override fun sourceName(): String = "CoinGecko"

    private fun symbolToCoinId(symbol: String): String =
        SYMBOL_TO_COIN_ID[symbol.uppercase()]
            ?: throw IllegalArgumentException("Unsupported crypto symbol: $symbol")

    companion object {
        private const val PRICE_SCALE = 8

        private val SYMBOL_TO_COIN_ID = mapOf(
            "BTC" to "bitcoin",
            "ETH" to "ethereum",
            "USDT" to "tether",
            "SOL" to "solana",
            "XRP" to "ripple",
            "ADA" to "cardano",
            "DOT" to "polkadot",
            "DOGE" to "dogecoin",
            "BNB" to "binancecoin",
            "USDC" to "usd-coin"
        )
    }
}
