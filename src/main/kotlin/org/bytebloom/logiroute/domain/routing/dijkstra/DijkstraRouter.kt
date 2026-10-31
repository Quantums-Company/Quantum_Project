package org.bytebloom.logiroute.domain.routing.dijkstra

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.common.RouteFinder
import org.bytebloom.logiroute.domain.routing.common.RouterValidator

class DijkstraRouter(
    private val warehouseGraph: WarehouseGraph
): RouterValidator(warehouseGraph), RouteFinder {

    private data class DijkstraState(
        val distanceByWarehouse: MutableMap<Warehouse, Double>,
        val visitedWarehouses: MutableSet<Warehouse>,
        val predecessorByWarehouse: MutableMap<Warehouse, Warehouse>
    )

    override fun findShortestPath(
        start: Warehouse,
        destination: Warehouse
    ): List<Warehouse>? {

        if (!areValidWarehouses(start, destination)) {
            return null
        }

        if (start.id == destination.id) {
            return listOf(start)
        }

        val searchState = createInitialState(start)

        return search(searchState, destination)
    }

    private fun createInitialState(
        startWarehouse: Warehouse
    ): DijkstraState {

        val distanceByWarehouse = mutableMapOf<Warehouse, Double>()

        for (warehouse in warehouseGraph.warehouses()) {
            distanceByWarehouse[warehouse] =
                Double.POSITIVE_INFINITY
        }

        distanceByWarehouse[startWarehouse] = 0.0

        return DijkstraState(
            distanceByWarehouse = distanceByWarehouse,
            visitedWarehouses = mutableSetOf(),
            predecessorByWarehouse = mutableMapOf()
        )
    }

    private fun findClosestUnvisitedWarehouse(
        distanceByWarehouse: Map<Warehouse, Double>,
        visitedWarehouses: Set<Warehouse>
    ): Warehouse? {

        var closestWarehouse: Warehouse? = null
        var shortestDistance = Double.POSITIVE_INFINITY

        for ((warehouse, distance) in distanceByWarehouse) {

            if (warehouse in visitedWarehouses) {
                continue
            }

            if (distance < shortestDistance) {
                shortestDistance = distance
                closestWarehouse = warehouse
            }
        }

        return closestWarehouse
    }

    private fun relaxNeighbors(
        currentWarehouse: Warehouse,
        currentDistance: Double,
        searchState: DijkstraState
    ) {
        for ((neighbor, distanceKm) in warehouseGraph.neighbors(currentWarehouse)) {

            if (neighbor in searchState.visitedWarehouses) {
                continue
            }

            val candidateDistance =
                currentDistance + distanceKm

            val knownShortestDistance =
                searchState.distanceByWarehouse.getValue(neighbor)

            if (candidateDistance < knownShortestDistance) {
                searchState.distanceByWarehouse[neighbor] = candidateDistance
                searchState.predecessorByWarehouse[neighbor] = currentWarehouse
            }
        }
    }

    private fun search(
        searchState: DijkstraState,
        destinationWarehouse: Warehouse
    ): List<Warehouse>? {

        while (true) {

            val currentWarehouse =
                findClosestUnvisitedWarehouse(
                    searchState.distanceByWarehouse,
                    searchState.visitedWarehouses
                ) ?: return null

            if (currentWarehouse.id == destinationWarehouse.id) {
                return reconstructPath(
                    searchState.predecessorByWarehouse,
                    destinationWarehouse
                )
            }

            searchState.visitedWarehouses.add(currentWarehouse)

            relaxNeighbors(
                currentWarehouse,
                searchState.distanceByWarehouse.getValue(currentWarehouse),
                searchState
            )
        }
    }
}
