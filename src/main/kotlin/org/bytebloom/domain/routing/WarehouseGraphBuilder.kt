package org.bytebloom.domain.routing

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse

class WarehouseGraphBuilder(
    private val warehouses: List<Warehouse>,
    private val routes: List<Route>
) {
    fun build(): GraphBuildResult {
        val graph = WarehouseGraph()
        addWarehouses(graph)
        val skipped = addValidRoutes(graph)
        return GraphBuildResult(graph, skipped)
    }

    private fun addWarehouses(graph: WarehouseGraph) {
        warehouses.forEach { graph.addWarehouse(it) }
    }

    private fun addValidRoutes(graph: WarehouseGraph): List<String> {
        val warehouseMap = warehouses.associateBy { it.id }
        val skipped = mutableListOf<String>()

        routes.forEach { route ->
            val originWarehouse = warehouseMap[route.originWarehouse.id]
            val destinationWarehouse = warehouseMap[route.destinationWarehouse.id]

            if (originWarehouse != null && destinationWarehouse != null) {
                graph.addRoute(originWarehouse, destinationWarehouse, route.distanceKm)
            } else {
                skipped.add(route.id)
            }
        }
        return skipped
    }
}
