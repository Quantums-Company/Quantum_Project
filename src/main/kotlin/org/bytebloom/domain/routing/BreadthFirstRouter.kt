package org.bytebloom.domain.routing

import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.WarehouseRepository

class BreadthFirstRouter(
    private val warehouseRepository: WarehouseRepository,
    private val routeRepository: RouteRepository
) {
    private fun buildAdjacencyMap(): Map<String, List<String>> {
        val routes = routeRepository.getAllRoutes()
        return routes.groupBy(
            keySelector = { it.originWarehouseId },
            valueTransform = { it.destinationWarehouseId }
        )
    }
    fun findShortestPath(startId: String, endId: String): List<String>? {

        if (startId == endId)
            return listOf(startId)

        val adjMap = buildAdjacencyMap()

        val allWarehouses = warehouseRepository.getAllWarehouses().map { it.id }.toSet()
        if (startId !in allWarehouses || endId !in allWarehouses)
            return null

        val queue = ArrayDeque<String>()
        val visited = mutableSetOf<String>()
        val parent = mutableMapOf<String, String>()
        queue.add(startId)
        visited.add(startId)


        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            if (current == endId) {
                return reconstructPath(parent, startId, endId)
            }

            val neighbors = adjMap[current] ?: emptyList()
            for (neighbor in neighbors) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    parent[neighbor] = current
                    queue.add(neighbor)
                }
            }
        }

        return null
    }
    // 3. دالة إعادة بناء الطريق بالعكس (Backtracking)
    private fun reconstructPath(parent: Map<String, String>, start: String, end: String): List<String> {
        val path = mutableListOf<String>()
        var current: String? = end

        while (current != null) {
            path.add(current)
            current = parent[current]
        }

        return path.reversed()
    }

}
