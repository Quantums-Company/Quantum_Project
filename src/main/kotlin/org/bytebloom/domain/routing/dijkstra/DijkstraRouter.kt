package org.bytebloom.domain.routing.dijkstra

import org.bytebloom.domain.routing.bfs.WarehouseGraph

class DijkstraRouter(
    private val graph: WarehouseGraph

) {

    private data class DijkstraState(
        val distances: MutableMap<String, Double>,
        val visited: MutableSet<String>,
        val parent: MutableMap<String, String>
    )

    private fun areValidWarehouses(
        startId: String,
        endId: String
    ): Boolean =
        graph.containsWarehouse(startId) &&
                graph.containsWarehouse(endId)

    private fun createInitialState(
        startId: String
    ): DijkstraState {
        val distances = mutableMapOf<String, Double>().withDefault { Double.MAX_VALUE }
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
        var lowestDistance = Double.MAX_VALUE

        for ((node, distance) in distances) {
            if (node !in visited && distance < lowestDistance) {
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
        for ((neighbor, weight) in graph.neighbors(current)) {
            if (neighbor in state.visited) {
                continue
            }

            val newDistance = currentDistance + weight
            if (newDistance < state.distances.getValue(neighbor)) {
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
            val current = findLowestUnvisitedNode(state.distances, state.visited) ?: break
            val currentDistance = state.distances.getValue(current)

            if (currentDistance == Double.MAX_VALUE) {
                break
            }

            if (current == endId) {
                return reconstructPath(state.parent, endId)
            }

            state.visited.add(current)
            relaxNeighbors(current, currentDistance, state)
        }

        return null
    }

    private fun reconstructPath(
        parent: Map<String, String>,
        endId: String
    ): List<String> {
        val path = mutableListOf<String>()
        var current: String? = endId

        while (current != null) {
            path.add(current)
            current = parent[current]
        }

        return path.asReversed()
    }

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
}
