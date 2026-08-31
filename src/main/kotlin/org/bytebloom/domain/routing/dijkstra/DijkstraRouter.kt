package org.bytebloom.domain.routing.dijkstra

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.routing.common.RouterValidator

class DijkstraRouter(
    private val graph: WarehouseGraph
): RouterValidator(graph), RouteFinder {

    private data class DijkstraState(
        val distances: MutableMap<Warehouse, Double>,
        val visited: MutableSet<Warehouse>,
        val parent: MutableMap<Warehouse, Warehouse>
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

        val state = createInitialState(start)

        return search(state, destination)
    }

    private fun createInitialState(
        startWarehouse: Warehouse
    ): DijkstraState {

        val distances = mutableMapOf<Warehouse, Double>()

        for (warehouse in graph.warehouses()) {
            distances[warehouse] =
                Double.POSITIVE_INFINITY
        }

        distances[startWarehouse] = 0.0

        return DijkstraState(
            distances = distances,
            visited = mutableSetOf(),
            parent = mutableMapOf()
        )
    }

    private fun findLowestUnvisitedNode(
        distances: Map<Warehouse, Double>,
        visited: Set<Warehouse>
    ): Warehouse? {

        var lowestNode: Warehouse? = null
        var lowestDistance = Double.POSITIVE_INFINITY

        for ((node, distance) in distances) {

            if (node in visited) {
                continue
            }

            if (distance < lowestDistance) {
                lowestDistance = distance
                lowestNode = node
            }
        }

        return lowestNode
    }

    private fun relaxNeighbors(
        current: Warehouse,
        currentDistance: Double,
        state: DijkstraState
    ) {
        for ((neighbor, distanceKm) in graph.neighbors(current)) {

            if (neighbor in state.visited) {
                continue
            }

            val newDistance =
                currentDistance + distanceKm

            val knownDistance =
                state.distances.getValue(neighbor)

            if (newDistance < knownDistance) {
                state.distances[neighbor] = newDistance
                state.parent[neighbor] = current
            }
        }
    }

    private fun search(
        state: DijkstraState,
        endWarehouse: Warehouse
    ): List<Warehouse>? {

        while (true) {

            val current =
                findLowestUnvisitedNode(
                    state.distances,
                    state.visited
                ) ?: return null

            if (current.id == endWarehouse.id) {
                return reconstructPath(
                    state.parent,
                    endWarehouse
                )
            }

            state.visited.add(current)

            relaxNeighbors(
                current,
                state.distances.getValue(current),
                state
            )
        }
    }
}
