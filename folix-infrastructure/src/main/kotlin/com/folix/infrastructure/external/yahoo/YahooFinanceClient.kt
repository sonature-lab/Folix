package com.folix.infrastructure.external.yahoo

import com.folix.application.port.out.MarketDataProvider
import com.folix.infrastructure.external.common.RetryableRestClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Yahoo Finance API를 통한 주식/펀드/채권 시세 제공자.
 */
@Component
class YahooFinanceClient(
    @Qualifier("yahooRestClient") private val restClient: RetryableRestClient
) : MarketDataProvider {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun fetchCurrentPrice(symbol: String): BigDecimal {
        logger.debug("Fetching current price for symbol: $symbol")

        val response = restClient.execute("Yahoo Finance fetchCurrentPrice($symbol)") {
            get()
                .uri("/v8/finance/chart/{symbol}", symbol)
                .retrieve()
                .body(YahooChartResponse::class.java)
                ?: throw IllegalStateException("Empty response from Yahoo Finance")
        }

        val price = response.chart.result?.firstOrNull()?.meta?.regularMarketPrice
            ?: throw IllegalStateException("No price data found for symbol: $symbol")

        return BigDecimal.valueOf(price).setScale(PRICE_SCALE, RoundingMode.HALF_UP)
    }

    override fun fetchHistoricalPrices(
        symbol: String,
        from: LocalDate,
        to: LocalDate
    ): List<Pair<LocalDate, BigDecimal>> {
        logger.debug("Fetching historical prices for $symbol from $from to $to")

        val period1 = from.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        val period2 = to.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

        val response = restClient.execute("Yahoo Finance fetchHistoricalPrices($symbol)") {
            get()
                .uri("/v8/finance/chart/{symbol}?period1={period1}&period2={period2}&interval=1d",
                    symbol, period1, period2)
                .retrieve()
                .body(YahooChartResponse::class.java)
                ?: throw IllegalStateException("Empty response from Yahoo Finance")
        }

        val result = response.chart.result?.firstOrNull()
            ?: throw IllegalStateException("No chart data for symbol: $symbol")

        val timestamps = result.timestamp ?: emptyList()
        val closes = result.indicators?.quote?.firstOrNull()?.close ?: emptyList()

        if (timestamps.size != closes.size) {
            throw IllegalStateException("Mismatched timestamps and prices for symbol: $symbol")
        }

        return timestamps.zip(closes)
            .mapNotNull { (timestamp, close) ->
                close?.let {
                    val date = Instant.ofEpochSecond(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val price = BigDecimal.valueOf(close).setScale(PRICE_SCALE, RoundingMode.HALF_UP)
                    date to price
                }
            }
    }

    override fun supports(assetType: String): Boolean =
        assetType.uppercase() in SUPPORTED_ASSET_TYPES

    override fun sourceName(): String = "YahooFinance"

    companion object {
        private const val PRICE_SCALE = 8
        private val SUPPORTED_ASSET_TYPES = setOf("STOCK", "FUND", "BOND")
    }
}
