package org.bytebloom.domain.tree.hierarchicalHub

import org.bytebloom.domain.model.Warehouse

class HubTree(
    val root: HubTreeNode
) {

    fun addChild(parent: HubTreeNode, child: HubTreeNode): Boolean {
        return parent.addChild(child)
    }

    fun findNode(warehouse: Warehouse): HubTreeNode? {
        return findNode(root, warehouse)
    }

    private fun findNode(
        current: HubTreeNode,
        warehouse: Warehouse
    ): HubTreeNode? {
        if (current.warehouse.id == warehouse.id) {
            return current
        }

        return current.children
            .asSequence()
            .mapNotNull { child -> findNode(child, warehouse) }
            .firstOrNull()
    }
}