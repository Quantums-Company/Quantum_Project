package org.bytebloom.domain.routing

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.util.Logger

class WarehouseGraphBuilder(
    private val warehouses: List<Warehouse>,
    private val routes: List<Route>
) {

    fun build(): WarehouseGraph {
        val graph = WarehouseGraph()

        addWarehouses(graph)
        addValidRoutes(graph)

        return graph
    }

    private fun addWarehouses(graph: WarehouseGraph) {
        warehouses.forEach { warehouse ->
            graph.addWarehouse(warehouse)
        }
    }

    private fun addValidRoutes(graph: WarehouseGraph) {
        val warehouseMap = warehouses.associateBy { it.id }

        routes.forEach { route ->
            val originWarehouse = warehouseMap[route.originWarehouse.id]
            val destinationWarehouse = warehouseMap[route.destinationWarehouse.id]

            if (originWarehouse != null && destinationWarehouse != null) {
                graph.addRoute(
                    originWarehouse = originWarehouse,
                    destinationWarehouse = destinationWarehouse,
                    distanceKm = route.distanceKm
                )
            } else {
                Logger.warning(
                    "Skipping route '${route.id}' because it references an unknown warehouse."
                )
            }
        }
    }
}
