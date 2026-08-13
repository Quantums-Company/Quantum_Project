package org.bytebloom.domain.routing.bfs

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.util.Logger

class WarehouseGraphBuilder(
    private val warehouses: List<Warehouse>,
    private val routes: List<Route>
) {

    fun buildWeightedGraph(): WarehouseGraph {
        val graph = WarehouseGraph()

        addWarehouses(graph)
        addValidRoutesForWeighted(graph)

        return graph
    }

    fun buildUnweightedGraph(): WarehouseGraph {
        val graph = WarehouseGraph()

        addWarehouses(graph)
        addValidRoutesForUnweighted(graph)

        return graph
    }

    private fun addWarehouses(graph: WarehouseGraph) {
        warehouses.forEach { warehouse ->
            graph.addWarehouse(warehouse.id)
        }
    }

    private fun addValidRoutesForWeighted(graph: WarehouseGraph) {
        val warehouseIds = warehouseIds()

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

    private fun addValidRoutesForUnweighted(graph: WarehouseGraph) {
        val warehouseIds = warehouseIds()

        routes.forEach { route ->
            if (isValidRoute(route, warehouseIds)) {
                graph.addRoute(
                    originId = route.originWarehouse.id,
                    destinationId = route.destinationWarehouse.id
                )
            }
        }
    }

    private fun warehouseIds(): Set<String> =
        warehouses.map { it.id }.toSet()

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