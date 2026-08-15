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
            graph.addWarehouse(warehouse.id)
        }
    }

    private fun addValidRoutes(graph: WarehouseGraph) {
        val warehouseIds = warehouses
            .map { it.id }
            .toSet()

        routes.forEach { route ->
            if (isValidRoute(route, warehouseIds)) {
                graph.addRoute(
                    originId = route.originWarehouse.id,
                    destinationId = route.destinationWarehouse.id,
                    distanceKm = route.distanceKm
                )
            }
        }
    }

    private fun isValidRoute(
        route: Route,
        warehouseIds: Set<String>
    ): Boolean {

        if (route.originWarehouse.id !in warehouseIds ||
            route.destinationWarehouse.id !in warehouseIds
        ) {
            Logger.warning(
                "Skipping route '${route.id}' because it references an unknown warehouse."
            )
            return false
        }

        return true
    }
}
