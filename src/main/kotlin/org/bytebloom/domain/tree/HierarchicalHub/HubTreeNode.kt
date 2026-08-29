package org.bytebloom.domain.tree.hierarchicalHub

import org.bytebloom.domain.model.Warehouse

class HubTreeNode(
    val warehouse: Warehouse,
    val type: HubType
) {
    private val _children = mutableListOf<HubTreeNode>()

    val children: List<HubTreeNode>
        get() = _children

    var parent: HubTreeNode? = null
        private set

    fun addChild(child: HubTreeNode): Boolean {
        if (!canAddChild(child) || child.parent != null) {
            return false
        }

        _children.add(child)
        child.parent = this
        return true
    }

    fun canAddChild(child: HubTreeNode): Boolean {
        return when (type) {
            HubType.GLOBAL -> child.type == HubType.REGIONAL
            HubType.REGIONAL -> child.type == HubType.LOCAL
            HubType.LOCAL -> false
        }
    }
}