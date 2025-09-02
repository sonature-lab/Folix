package com.folix.domain.taxonomy

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

class ClassificationTest : DescribeSpec({

    describe("Classification") {

        val assetId = UUID.randomUUID()
        val nodeId = UUID.randomUUID()

        describe("생성") {
            it("should_create_with_default_weight_100") {
                val c = Classification(assetId = assetId, nodeId = nodeId)
                c.weight.setScale(2, RoundingMode.HALF_UP) shouldBe
                    BigDecimal(100).setScale(2, RoundingMode.HALF_UP)
            }

            it("should_create_with_custom_weight") {
                val c = Classification(
                    assetId = assetId,
                    nodeId = nodeId,
                    weight = BigDecimal("60.00")
                )
                c.weight shouldBe BigDecimal("60.00")
            }

            it("should_fail_when_weight_is_zero") {
                shouldThrow<IllegalArgumentException> {
                    Classification(
                        assetId = assetId,
                        nodeId = nodeId,
                        weight = BigDecimal.ZERO
                    )
                }
            }

            it("should_fail_when_weight_is_negative") {
                shouldThrow<IllegalArgumentException> {
                    Classification(
                        assetId = assetId,
                        nodeId = nodeId,
                        weight = BigDecimal("-10")
                    )
                }
            }

            it("should_fail_when_weight_exceeds_100") {
                shouldThrow<IllegalArgumentException> {
                    Classification(
                        assetId = assetId,
                        nodeId = nodeId,
                        weight = BigDecimal("100.01")
                    )
                }
            }
        }

        describe("가중치 검증") {
            it("should_pass_when_weights_sum_to_100") {
                val nodeA = UUID.randomUUID()
                val nodeB = UUID.randomUUID()

                val classifications = listOf(
                    Classification(assetId = assetId, nodeId = nodeA, weight = BigDecimal("70.00")),
                    Classification(assetId = assetId, nodeId = nodeB, weight = BigDecimal("30.00"))
                )

                Classification.validateWeights(classifications) // 예외 없음
            }

            it("should_fail_when_weights_do_not_sum_to_100") {
                val nodeA = UUID.randomUUID()
                val nodeB = UUID.randomUUID()

                val classifications = listOf(
                    Classification(assetId = assetId, nodeId = nodeA, weight = BigDecimal("60.00")),
                    Classification(assetId = assetId, nodeId = nodeB, weight = BigDecimal("30.00"))
                )

                shouldThrow<IllegalArgumentException> {
                    Classification.validateWeights(classifications)
                }
            }

            it("should_pass_with_empty_list") {
                Classification.validateWeights(emptyList()) // 예외 없음
            }

            it("should_pass_with_single_100_percent") {
                val classifications = listOf(
                    Classification(assetId = assetId, nodeId = nodeId, weight = BigDecimal("100.00"))
                )
                Classification.validateWeights(classifications) // 예외 없음
            }

            it("should_pass_with_multiple_small_weights") {
                val classifications = listOf(
                    Classification(assetId = assetId, nodeId = UUID.randomUUID(), weight = BigDecimal("25.00")),
                    Classification(assetId = assetId, nodeId = UUID.randomUUID(), weight = BigDecimal("25.00")),
                    Classification(assetId = assetId, nodeId = UUID.randomUUID(), weight = BigDecimal("25.00")),
                    Classification(assetId = assetId, nodeId = UUID.randomUUID(), weight = BigDecimal("25.00"))
                )
                Classification.validateWeights(classifications) // 예외 없음
            }
        }
    }
})
