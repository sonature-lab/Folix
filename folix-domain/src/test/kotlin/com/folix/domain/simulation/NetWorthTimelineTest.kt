package com.folix.domain.simulation

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class NetWorthTimelineTest : DescribeSpec({

    describe("NetWorthTimeline") {

        val timeline = NetWorthTimeline.of(
            LocalDate.of(2025, 1, 1) to BigDecimal("100000"),
            LocalDate.of(2025, 4, 1) to BigDecimal("110000"),
            LocalDate.of(2025, 7, 1) to BigDecimal("105000"),
            LocalDate.of(2025, 10, 1) to BigDecimal("120000"),
            LocalDate.of(2025, 12, 31) to BigDecimal("115000")
        )

        describe("기본 속성") {
            it("should_return_start_and_end_dates") {
                timeline.startDate shouldBe LocalDate.of(2025, 1, 1)
                timeline.endDate shouldBe LocalDate.of(2025, 12, 31)
            }

            it("should_return_start_and_end_values") {
                timeline.startValue shouldBe BigDecimal("100000")
                timeline.endValue shouldBe BigDecimal("115000")
            }

            it("should_return_peak_and_trough") {
                timeline.peakValue shouldBe BigDecimal("120000")
                timeline.troughValue shouldBe BigDecimal("100000")
            }
        }

        describe("변화율") {
            it("should_calculate_total_change_rate") {
                // (115000 - 100000) / 100000 * 100 = 15%
                timeline.totalChangeRate().setScale(2, RoundingMode.HALF_UP) shouldBe BigDecimal("15.00")
            }
        }

        describe("서브 타임라인") {
            it("should_extract_sub_timeline") {
                val sub = timeline.subTimeline(
                    LocalDate.of(2025, 4, 1),
                    LocalDate.of(2025, 10, 1)
                )
                sub.entries.size shouldBe 3
                sub.startValue shouldBe BigDecimal("110000") // April 1st value
            }

            it("should_fail_when_no_entries_in_range") {
                shouldThrow<IllegalArgumentException> {
                    timeline.subTimeline(
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)
                    )
                }
            }
        }

        describe("유효성 검증") {
            it("should_fail_when_empty") {
                shouldThrow<IllegalArgumentException> {
                    NetWorthTimeline(emptyList())
                }
            }
        }
    }
})
