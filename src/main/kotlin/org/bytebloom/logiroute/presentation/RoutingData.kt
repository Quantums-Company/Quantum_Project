package org.bytebloom.logiroute.presentation

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.routing.WarehouseGraph

/** The warehouses and the road graph built from them, shared by every screen that plans routes. */
internal class RoutingData(
    val warehouses: List<Warehouse>,
    val graph: WarehouseGraph,
    val skippedRouteIds: List<String>
) {
    /** Finds a warehouse ignoring letter case and surrounding spaces. */
    fun warehouseById(id: String): Warehouse =
        warehouses.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")
}

