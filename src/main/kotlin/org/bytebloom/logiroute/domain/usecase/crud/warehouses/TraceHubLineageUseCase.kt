package org.bytebloom.logiroute.domain.usecase.crud.warehouses

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.tree.hub.HubTree

class TraceHubLineageUseCase(
    private val hubTree: HubTree
) {
    operator fun invoke(warehouse: Warehouse): List<Warehouse> =
        hubTree.lineageFrom(warehouse)
}