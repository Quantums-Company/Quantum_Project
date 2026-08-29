package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.tree.hierarchicalHub.HubTree

class TraceHubLineageUseCase(
    private val hubTree: HubTree
) {
    operator fun invoke(warehouse: Warehouse): List<Warehouse> {
        val node = hubTree.findNode(warehouse)
            ?: return emptyList()

        return generateSequence(node) { it.parent }
            .map { it.warehouse }
            .toList()
    }
}