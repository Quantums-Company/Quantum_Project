package org.bytebloom.domain.routing.dijkstra

import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.common.RouterValidator

class DijkstraRouter(
    private val graph: WarehouseGraph
): RouterValidator(graph) {

    private data class DijkstraState(
        val distances: MutableMap<String, Double>,
        val visited: MutableSet<String>,
        val parent: MutableMap<String, String>
    )

    fun findShortestPath(
        startId: String,
        endId: String
    ): List<String>? {

        if (!areValidWarehouses(startId, endId)) {
            return null
        }

        if (startId == endId) {
            return listOf(startId)
        }

        val state = createInitialState(startId)

        return search(state, endId)
    }

    private fun createInitialState(
        startId: String
    ): DijkstraState {

        val distances = mutableMapOf<String, Double>()

        for (warehouseId in graph.warehouseIds()) {
            distances[warehouseId] =
                Double.POSITIVE_INFINITY
        }

        distances[startId] = 0.0

        return DijkstraState(
            distances = distances,
            visited = mutableSetOf(),
            parent = mutableMapOf()
        )
    }

    private fun findLowestUnvisitedNode(
        distances: Map<String, Double>,
        visited: Set<String>
    ): String? {

        var lowestNode: String? = null
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
        current: String,
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
        endId: String
    ): List<String>? {

        while (true) {

            val current =
                findLowestUnvisitedNode(
                    state.distances,
                    state.visited
                ) ?: return null

            if (current == endId) {
                return reconstructPath(
                    state.parent,
                    endId
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
