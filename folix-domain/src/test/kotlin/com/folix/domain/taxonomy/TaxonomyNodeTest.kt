package com.folix.domain.taxonomy

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TaxonomyNodeTest : DescribeSpec({

    describe("TaxonomyNode") {

        describe("생성") {
            it("should_create_root_node") {
                val root = TaxonomyNode.root("Asset Type", TaxonomyType.ASSET_TYPE)
                root.name shouldBe "Asset Type"
                root.taxonomyType shouldBe TaxonomyType.ASSET_TYPE
                root.level shouldBe 0
                root.isRoot shouldBe true
                root.isLeaf shouldBe true
                root.parentId shouldBe null
            }

            it("should_fail_when_name_is_blank") {
                shouldThrow<IllegalArgumentException> {
                    TaxonomyNode.root("", TaxonomyType.REGION)
                }
            }

            it("should_fail_when_level_is_negative") {
                shouldThrow<IllegalArgumentException> {
                    TaxonomyNode(
                        name = "Test",
                        taxonomyType = TaxonomyType.REGION,
                        level = -1
                    )
                }
            }
        }

        describe("트리 구조") {
            it("should_add_child_with_correct_parent_and_level") {
                val root = TaxonomyNode.root("Region", TaxonomyType.REGION)
                val updated = root.addChild("North America")

                updated.children shouldHaveSize 1
                updated.isLeaf shouldBe false

                val child = updated.children.first()
                child.name shouldBe "North America"
                child.parentId shouldBe root.id
                child.level shouldBe 1
                child.taxonomyType shouldBe TaxonomyType.REGION
                child.isRoot shouldBe false
            }

            it("should_build_multi_level_tree") {
                val root = TaxonomyNode.root("Region", TaxonomyType.REGION)
                    .addChild("North America")
                    .addChild("Europe")

                root.children shouldHaveSize 2

                val na = root.children.first()
                val updatedRoot = root.copy(
                    children = listOf(
                        na.addChild("United States").addChild("Canada"),
                        root.children[1]
                    )
                )

                val us = updatedRoot.children[0].children[0]
                us.name shouldBe "United States"
                us.level shouldBe 2
                us.parentId shouldBe na.id
            }

            it("should_preserve_immutability_when_adding_child") {
                val original = TaxonomyNode.root("Industry", TaxonomyType.INDUSTRY)
                val updated = original.addChild("Technology")

                original.children shouldHaveSize 0
                updated.children shouldHaveSize 1
            }
        }

        describe("검색") {
            val tree = TaxonomyNode.root("Region", TaxonomyType.REGION)
                .addChild("North America")
                .let { root ->
                    val na = root.children[0].addChild("United States")
                    root.copy(children = listOf(na) + root.children.drop(1))
                }
                .addChild("Europe")

            it("should_find_node_by_name") {
                tree.findByName("Region") shouldNotBe null
                tree.findByName("North America") shouldNotBe null
                tree.findByName("United States") shouldNotBe null
                tree.findByName("Europe") shouldNotBe null
            }

            it("should_return_null_for_nonexistent_name") {
                tree.findByName("Asia") shouldBe null
            }
        }

        describe("flatten") {
            it("should_flatten_tree_to_list") {
                val tree = TaxonomyNode.root("Asset Type", TaxonomyType.ASSET_TYPE)
                    .addChild("Equity")
                    .addChild("Fixed Income")

                val flattened = tree.flatten()
                flattened shouldHaveSize 3
                flattened[0].name shouldBe "Asset Type"
                flattened.map { it.name } shouldBe listOf("Asset Type", "Equity", "Fixed Income")
            }
        }
    }
})
