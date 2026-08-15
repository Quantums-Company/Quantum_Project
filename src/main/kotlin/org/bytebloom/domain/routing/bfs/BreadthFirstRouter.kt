package org.bytebloom.domain.routing.bfs

import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.common.RouterValidator

class BreadthFirstRouter(
    private val graph: WarehouseGraph
) : RouterValidator(graph) {

    private data class SearchState(
        val queue: ArrayDeque<String>,
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

        val state = createSearchState(startId)

        return search(state, endId)
    }

    private fun createSearchState(
        startId: String
    ): SearchState {

        val queue = ArrayDeque<String>()
        val visited = mutableSetOf<String>()

        queue.addLast(startId)
        visited.add(startId)

        return SearchState(
            queue = queue,
            visited = visited,
            parent = mutableMapOf()
        )
    }

    private fun search(
        state: SearchState,
        endId: String
    ): List<String>? {

        while (state.queue.isNotEmpty()) {
            val current = state.queue.removeFirst()

            if (current == endId) {
                return reconstructPath(
                    state.parent,
                    endId
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
        current: String,
        state: SearchState
    ) {
        for (neighbor in graph.neighbors(current).keys) {

            if (neighbor in state.visited) {
                continue
            }

            state.visited.add(neighbor)
            state.parent[neighbor] = current
            state.queue.addLast(neighbor)
        }
    }
}