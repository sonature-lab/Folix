package com.folix.infrastructure.external.yahoo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Yahoo Finance Chart API 응답.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class YahooChartResponse(
    val chart: ChartData
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ChartData(
        val result: List<ChartResult>?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ChartResult(
        val meta: Meta,
        val timestamp: List<Long>?,
        val indicators: Indicators?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Meta(
        val regularMarketPrice: Double?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Indicators(
        val quote: List<Quote>?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Quote(
        val close: List<Double?>?
    )
}
