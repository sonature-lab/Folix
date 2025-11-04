package com.folix.domain.simulation

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 시뮬레이션 결과를 담는 데이터 클래스.
 *
 * 몬테카를로 시뮬레이션의 퍼센타일별 결과와 시계열을 포함한다.
 *
 * @property scenarioName 시나리오 이름
 * @property iterations 시뮬레이션 횟수
 * @property percentiles 퍼센타일별 최종 자산 가치
 * @property monthlyTimeline 월별 중앙값(p50) 시계열
 */
data class SimulationResult(
    val scenarioName: String,
    val iterations: Int,
    val percentiles: Percentiles,
    val monthlyTimeline: List<MonthlyProjection>
) {

    /**
     * 퍼센타일별 결과.
     */
    data class Percentiles(
        val p5: BigDecimal,
        val p25: BigDecimal,
        val p50: BigDecimal,
        val p75: BigDecimal,
        val p95: BigDecimal
    ) {
        override fun toString(): String =
            "p5=$p5, p25=$p25, p50=$p50, p75=$p75, p95=$p95"
    }

    /**
     * 월별 예측 데이터 포인트.
     */
    data class MonthlyProjection(
        val month: Int,
        val p5: BigDecimal,
        val p25: BigDecimal,
        val p50: BigDecimal,
        val p75: BigDecimal,
        val p95: BigDecimal
    )

    companion object {
        private const val RESULT_SCALE = 2

        /**
         * 시뮬레이션 결과 배열에서 퍼센타일을 추출한다.
         *
         * @param sortedValues 정렬된 최종 가치 목록
         * @return [Percentiles]
         */
        fun extractPercentiles(sortedValues: List<BigDecimal>): Percentiles {
            require(sortedValues.isNotEmpty()) { "Values must not be empty" }
            val n = sortedValues.size
            return Percentiles(
                p5 = sortedValues[percentileIndex(n, 5)].setScale(RESULT_SCALE, RoundingMode.HALF_UP),
                p25 = sortedValues[percentileIndex(n, 25)].setScale(RESULT_SCALE, RoundingMode.HALF_UP),
                p50 = sortedValues[percentileIndex(n, 50)].setScale(RESULT_SCALE, RoundingMode.HALF_UP),
                p75 = sortedValues[percentileIndex(n, 75)].setScale(RESULT_SCALE, RoundingMode.HALF_UP),
                p95 = sortedValues[percentileIndex(n, 95)].setScale(RESULT_SCALE, RoundingMode.HALF_UP)
            )
        }

        private fun percentileIndex(size: Int, percentile: Int): Int {
            val index = (percentile / 100.0 * size).toInt()
            return index.coerceIn(0, size - 1)
        }
    }
}
