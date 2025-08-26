package com.folix.domain.portfolio

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 자산 배분 항목을 나타내는 값 객체.
 *
 * 포트폴리오 내에서 특정 카테고리(유형, 통화, 계좌 등)가 차지하는 비중을 표현한다.
 *
 * @property category 분류 이름 (예: "STOCK", "USD", "Binance")
 * @property value 해당 분류의 총 평가금액
 * @property weight 포트폴리오 내 비중 (%, 0~100)
 */
data class Allocation(
    val category: String,
    val value: BigDecimal,
    val weight: BigDecimal
) {

    init {
        require(category.isNotBlank()) { "Allocation category must not be blank" }
        require(weight >= BigDecimal.ZERO && weight <= BigDecimal(100)) {
            "Weight must be between 0 and 100: $weight"
        }
    }

    override fun toString(): String =
        "$category: ${weight.setScale(2, RoundingMode.HALF_UP)}%"

    companion object {
        /**
         * 포지션 목록에서 그룹별 배분을 계산한다.
         *
         * @param positions 포지션 목록
         * @param groupBy 그룹핑 기준 함수
         * @return 배분 목록
         */
        fun fromPositions(
            positions: List<Position>,
            groupBy: (Position) -> String
        ): List<Allocation> {
            val totalValue = positions.sumOf { it.marketValue }
            if (totalValue.compareTo(BigDecimal.ZERO) == 0) return emptyList()

            return positions
                .groupBy(groupBy)
                .map { (category, grouped) ->
                    val groupValue = grouped.sumOf { it.marketValue }
                    val weight = groupValue.divide(totalValue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100))
                    Allocation(category, groupValue, weight)
                }
                .sortedByDescending { it.weight }
        }
    }
}
