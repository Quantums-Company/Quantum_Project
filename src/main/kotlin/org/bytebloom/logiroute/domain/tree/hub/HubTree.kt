package org.bytebloom.logiroute.domain.tree.hub

import org.bytebloom.logiroute.domain.model.Warehouse

class HubTree(
    val root: HubTreeNode
) {

    fun findNode(
        warehouse: Warehouse
    ): HubTreeNode? =
        findNode(root, warehouse.id)

    private fun findNode(
        current: HubTreeNode,
        warehouseId: String
    ): HubTreeNode? {

        if (current.warehouse.id.equals(warehouseId, ignoreCase = true)) {
            return current
        }

        return current.children
            .asSequence()
            .mapNotNull { child ->
                findNode(child, warehouseId)
            }
            .firstOrNull()
    }

    fun lineageFrom(
        warehouse: Warehouse
    ): List<Warehouse> {

        val node =
            findNode(warehouse)
                ?: return emptyList()

        return generateSequence(node) { it.parent }
            .map { it.warehouse }
            .toList()
    }
}
