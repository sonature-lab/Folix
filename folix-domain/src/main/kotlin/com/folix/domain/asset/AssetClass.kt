package com.folix.domain.asset

/**
 * 자산 분류를 나타내는 열거형.
 *
 * [AssetType]이 세부 유형이라면, [AssetClass]는 상위 자산 군을 나타낸다.
 */
enum class AssetClass {
    EQUITY,
    FIXED_INCOME,
    COMMODITY,
    CRYPTOCURRENCY,
    REAL_ESTATE,
    CASH_EQUIVALENT,
    ALTERNATIVE
}
