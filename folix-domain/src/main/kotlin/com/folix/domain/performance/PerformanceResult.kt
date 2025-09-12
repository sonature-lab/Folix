package com.folix.domain.performance

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 포트폴리오 성과 결과를 담는 데이터 클래스.
 *
 * TWR, IRR, 리스크 지표를 통합하여 제공한다.
 *
 * @property twr 시간가중수익률 (소수)
 * @property irr 내부수익률 (소수, nullable — 수렴 실패 시 null)
 * @property annualizedVolatility 연환산 변동성 (소수)
 * @property sharpeRatio 샤프비율
 * @property maxDrawdown 최대낙폭 (소수, 음수)
 * @property periodStart 분석 시작일
 * @property periodEnd 분석 종료일
 */
data class PerformanceResult(
    val twr: BigDecimal,
    val irr: BigDecimal?,
    val annualizedVolatility: BigDecimal,
    val sharpeRatio: BigDecimal,
    val maxDrawdown: BigDecimal,
    val periodStart: LocalDate,
    val periodEnd: LocalDate
) {
    /** TWR을 백분율(%)로 반환 */
    val twrPercent: BigDecimal get() = twr.multiply(BigDecimal(100))

    /** IRR을 백분율(%)로 반환 */
    val irrPercent: BigDecimal? get() = irr?.multiply(BigDecimal(100))

    /** MDD를 백분율(%)로 반환 */
    val maxDrawdownPercent: BigDecimal get() = maxDrawdown.multiply(BigDecimal(100))

    /** 변동성을 백분율(%)로 반환 */
    val volatilityPercent: BigDecimal get() = annualizedVolatility.multiply(BigDecimal(100))
}
