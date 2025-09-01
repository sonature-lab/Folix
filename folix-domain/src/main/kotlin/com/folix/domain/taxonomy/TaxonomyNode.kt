package com.folix.domain.taxonomy

import java.util.UUID

/**
 * 분류 체계의 트리 노드를 나타내는 엔티티.
 *
 * 자기 참조(self-referencing)로 계층 구조를 형성한다.
 * 예: REGION → North America → United States
 *
 * @property id 노드 고유 ID
 * @property name 노드 이름 (예: "North America", "Technology")
 * @property taxonomyType 분류 축
 * @property parentId 부모 노드 ID. 루트 노드이면 null
 * @property level 트리 깊이 (루트 = 0)
 * @property children 자식 노드 목록
 */
data class TaxonomyNode(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val taxonomyType: TaxonomyType,
    val parentId: UUID? = null,
    val level: Int = 0,
    val children: List<TaxonomyNode> = emptyList()
) {

    init {
        require(name.isNotBlank()) { "TaxonomyNode name must not be blank" }
        require(level >= 0) { "Level must be non-negative: $level" }
    }

    /** 루트 노드인지 확인한다. */
    val isRoot: Boolean get() = parentId == null

    /** 리프 노드(자식 없음)인지 확인한다. */
    val isLeaf: Boolean get() = children.isEmpty()

    /**
     * 자식 노드를 추가한 새 노드를 반환한다. (불변)
     *
     * 자식의 taxonomyType과 level을 자동으로 설정한다.
     */
    fun addChild(childName: String, childId: UUID = UUID.randomUUID()): TaxonomyNode {
        val child = TaxonomyNode(
            id = childId,
            name = childName,
            taxonomyType = taxonomyType,
            parentId = id,
            level = level + 1
        )
        return copy(children = children + child)
    }

    /**
     * 이 노드 이하 트리에서 이름으로 노드를 찾는다. (DFS)
     */
    fun findByName(targetName: String): TaxonomyNode? {
        if (name == targetName) return this
        return children.firstNotNullOfOrNull { it.findByName(targetName) }
    }

    /**
     * 이 노드 이하의 모든 노드를 평탄화하여 반환한다.
     */
    fun flatten(): List<TaxonomyNode> =
        listOf(this) + children.flatMap { it.flatten() }

    override fun toString(): String = "${"  ".repeat(level)}$name (${taxonomyType.name})"

    companion object {
        /**
         * 루트 노드를 생성하는 편의 팩토리.
         */
        fun root(name: String, taxonomyType: TaxonomyType): TaxonomyNode =
            TaxonomyNode(name = name, taxonomyType = taxonomyType, level = 0)
    }
}
