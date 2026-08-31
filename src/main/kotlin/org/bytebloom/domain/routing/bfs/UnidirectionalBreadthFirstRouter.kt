package org.bytebloom.domain.routing.bfs

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.routing.common.RouterValidator

class UnidirectionalBreadthFirstRouter(
    private val graph: WarehouseGraph
) : RouterValidator(graph), RouteFinder {

    var evaluatedWarehouses = 0

    private class SearchState(start: Warehouse) {
        val queue = ArrayDeque<Warehouse>().apply { addLast(start) }
        val visited = mutableSetOf(start)
        val parent = mutableMapOf<Warehouse, Warehouse>()
    }

    override fun findShortestPath(
        start: Warehouse,
        destination: Warehouse
    ): List<Warehouse>? {

        evaluatedWarehouses = 0

        if (!areValidWarehouses(start, destination)) {
            return null
        }

        if (start.id == destination.id) {
            return listOf(start)
        }

        return search(SearchState(start), destination)
    }

    private fun search(
        state: SearchState,
        endWarehouse: Warehouse
    ): List<Warehouse>? {

        while (state.queue.isNotEmpty()) {
            val current = state.queue.removeFirst()
            evaluatedWarehouses++

            if (current.id == endWarehouse.id) {
                return reconstructPath(
                    state.parent,
                    endWarehouse
                )
            }

            visitNeighbors(current, state)
        }

        return null
    }

    private fun visitNeighbors(
        currentWarehouse: Warehouse,
        state: SearchState
    ) {
        for (neighbor in graph.neighbors(currentWarehouse).keys) {
            if (neighbor in state.visited) continue

            state.visited.add(neighbor)
            state.parent[neighbor] = currentWarehouse
            state.queue.addLast(neighbor)
        }
    }
}