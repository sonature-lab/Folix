package com.folix.domain.money

/**
 * 통화를 나타내는 값 객체.
 *
 * ISO 4217 표준 통화 코드 및 크립토 통화를 지원한다.
 * companion object에 주요 통화가 사전 정의되어 있으며,
 * [of] 팩토리 메서드로 커스텀 통화를 생성할 수 있다.
 *
 * @property code 통화 코드 (예: "USD", "KRW", "BTC")
 */
@ConsistentCopyVisibility
data class Currency private constructor(val code: String) {

    init {
        require(code.isNotBlank()) { "Currency code must not be blank" }
        require(code.length in 2..5) { "Currency code must be 2-5 characters: $code" }
    }

    override fun toString(): String = code

    companion object {
        // Fiat currencies (ISO 4217)
        val USD = Currency("USD")
        val KRW = Currency("KRW")
        val EUR = Currency("EUR")
        val JPY = Currency("JPY")
        val GBP = Currency("GBP")
        val CHF = Currency("CHF")
        val CAD = Currency("CAD")
        val AUD = Currency("AUD")
        val SGD = Currency("SGD")
        val HKD = Currency("HKD")
        val CNY = Currency("CNY")

        // Crypto currencies
        val BTC = Currency("BTC")
        val ETH = Currency("ETH")
        val USDT = Currency("USDT")
        val USDC = Currency("USDC")

        private val CACHE = java.util.concurrent.ConcurrentHashMap<String, Currency>().apply {
            listOf(USD, KRW, EUR, JPY, GBP, CHF, CAD, AUD, SGD, HKD, CNY, BTC, ETH, USDT, USDC)
                .forEach { put(it.code, it) }
        }

        /**
         * 통화 코드로 [Currency] 인스턴스를 생성하거나 캐시에서 반환한다.
         *
         * @param code 통화 코드 (대소문자 무관, 내부에서 대문자로 변환)
         * @return [Currency] 인스턴스
         */
        fun of(code: String): Currency {
            val normalized = code.uppercase().trim()
            return CACHE.getOrPut(normalized) { Currency(normalized) }
        }
    }
}
