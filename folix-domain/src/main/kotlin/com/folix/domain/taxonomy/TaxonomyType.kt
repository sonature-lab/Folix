package com.folix.domain.taxonomy

/**
 * 분류 체계의 축(axis)을 나타내는 열거형.
 *
 * 자산을 다양한 관점에서 분류할 수 있도록 여러 축을 정의한다.
 */
enum class TaxonomyType {
    /** 자산 유형별 분류 (주식, 채권, 크립토 등) */
    ASSET_TYPE,
    /** 지역별 분류 (북미, 유럽, 아시아 등) */
    REGION,
    /** 산업별 분류 (기술, 금융, 헬스케어 등) */
    INDUSTRY,
    /** 통화별 분류 (USD, KRW, BTC 등) */
    CURRENCY
}
