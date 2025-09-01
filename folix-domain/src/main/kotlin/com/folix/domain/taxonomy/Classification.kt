package com.folix.domain.taxonomy

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

/**
 * 자산과 분류 노드의 매핑을 나타내는 값 객체.
 *
 * 하나의 자산이 여러 분류 노드에 가중치(%)로 매핑될 수 있다.
 * 예: AAPL → Region: "North America" 100%, Industry: "Technology" 80% + "Consumer" 20%
 *
 * @property id 매핑 고유 ID
 * @property assetId 자산 ID
 * @property nodeId 분류 노드 ID
 * @property weight 가중치 (%) — 0.00 ~ 100.00
 */
data class Classification(
    val id: UUID = UUID.randomUUID(),
    val assetId: UUID,
    val nodeId: UUID,
    val weight: BigDecimal = BigDecimal(100).setScale(2, RoundingMode.HALF_UP)
) {

    init {
        require(weight > BigDecimal.ZERO) {
            "Classification weight must be positive: $weight"
        }
        require(weight <= BigDecimal(100).setScale(2, RoundingMode.HALF_UP)) {
            "Classification weight must not exceed 100: $weight"
        }
    }

    override fun toString(): String =
        "Classification(asset=$assetId, node=$nodeId, weight=${weight.setScale(2, RoundingMode.HALF_UP)}%)"

    companion object {
        /**
         * 동일 taxonomyType에 대한 분류 가중치 합이 100%인지 검증한다.
         *
         * @param classifications 동일 자산 + 동일 taxonomyType의 분류 목록
         * @throws IllegalArgumentException 가중치 합이 100%가 아닌 경우
         */
        fun validateWeights(classifications: List<Classification>) {
            if (classifications.isEmpty()) return
            val totalWeight = classifications
                .fold(BigDecimal.ZERO) { acc, c -> acc.add(c.weight) }
                .setScale(2, RoundingMode.HALF_UP)
            require(totalWeight.compareTo(BigDecimal(100).setScale(2, RoundingMode.HALF_UP)) == 0) {
                "Classification weights must sum to 100%, but got $totalWeight%"
            }
        }
    }
}
