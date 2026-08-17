package org.bytebloom.domain.routing.bfs

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.common.RouterValidator

class UnidirectionalBreadthFirstRouter(
    private val graph: WarehouseGraph
) : RouterValidator(graph) {

    private data class SearchState(
        val queue: ArrayDeque<Warehouse>,
        val visited: MutableSet<Warehouse>,
        val parent: MutableMap<Warehouse, Warehouse>
    )

    fun findShortestPath(
        startWarehouse: Warehouse,
        endWarehouse: Warehouse
    ): List<Warehouse>? {

        if (!areValidWarehouses(startWarehouse, endWarehouse)) {
            return null
        }

        if (startWarehouse.id == endWarehouse.id) {
            return listOf(startWarehouse)
        }

        val state = createSearchState(startWarehouse)

        return search(state, endWarehouse)
    }

    private fun createSearchState(
        startWarehouse: Warehouse
    ): SearchState {

        val queue = ArrayDeque<Warehouse>()
        val visited = mutableSetOf<Warehouse>()

        queue.addLast(startWarehouse)
        visited.add(startWarehouse)

        return SearchState(
            queue = queue,
            visited = visited,
            parent = mutableMapOf()
        )
    }

    private fun search(
        state: SearchState,
        endWarehouse: Warehouse
    ): List<Warehouse>? {

        while (state.queue.isNotEmpty()) {
            val current = state.queue.removeFirst()

            if (current.id == endWarehouse.id) {
                return reconstructPath(
                    state.parent,
                    endWarehouse
                )
            }

            visitNeighbors(
                current,
                state
            )
        }

        return null
    }

    private fun visitNeighbors(
        currentWarehouse: Warehouse,
        state: SearchState
    ) {
        for (neighbor in graph.neighbors(currentWarehouse).keys) {

            if (neighbor in state.visited) {
                continue
            }

            state.visited.add(neighbor)
            state.parent[neighbor] = currentWarehouse
            state.queue.addLast(neighbor)
        }
    }
}