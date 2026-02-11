package com.folix.infrastructure.external.coingecko

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * CoinGecko Market Chart API 응답.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CoinGeckoMarketChartResponse(
    val prices: List<List<Number>>
)

/**
 * CoinGecko Simple Price API 응답.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CoinGeckoSimplePriceResponse(
    val prices: Map<String, Map<String, Double>>
)
