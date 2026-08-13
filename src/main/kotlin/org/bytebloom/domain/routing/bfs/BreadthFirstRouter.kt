package org.bytebloom.domain.routing.bfs

class BreadthFirstRouter(
    private val graph: WarehouseGraph
) {

    private data class SearchState(
        val queue: ArrayDeque<String>,
        val visited: MutableSet<String>,
        val parent: MutableMap<String, String>
    )

    private fun areValidWarehouses(
        startId: String,
        endId: String
    ): Boolean =
        graph.containsWarehouse(startId) &&
                graph.containsWarehouse(endId)

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
        for (neighbor in graph.neighbors(current)) {

            if (neighbor.key in state.visited) {
                continue
            }

            state.visited.add(neighbor.key)
            state.parent[neighbor.key] = current
            state.queue.addLast(neighbor.key)
        }
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

        val state = createSearchState(startId)

        return search(state, endId)
    }
}