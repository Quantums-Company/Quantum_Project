package org.bytebloom.domain.routing

import org.bytebloom.domain.model.Warehouse

class BidirectionalBFSConnectivityChecker(
    private val graph: WarehouseGraph
) {

    fun isReachable(
        origin: Warehouse,
        destination: Warehouse
    ): Boolean {

        if (!graph.containsWarehouse(origin) ||
            !graph.containsWarehouse(destination)
        ) {
            return false
        }

        if (origin == destination) {
            return true
        }

        val forwardSearch = SearchState(
            queue = ArrayDeque<Warehouse>().apply {
                addLast(origin)
            },
            visited = mutableSetOf(origin)
        )

        val backwardSearch = SearchState(
            queue = ArrayDeque<Warehouse>().apply {
                addLast(destination)
            },
            visited = mutableSetOf(destination)
        )

        while (
            forwardSearch.queue.isNotEmpty() &&
            backwardSearch.queue.isNotEmpty()
        ) {

            if (expandForward(
                    current = forwardSearch,
                    opposite = backwardSearch
                )
            ) {
                return true
            }

            if (expandBackward(
                    current = backwardSearch,
                    opposite = forwardSearch
                )
            ) {
                return true
            }
        }

        return false
    }

    private data class SearchState(
        val queue: ArrayDeque<Warehouse>,
        val visited: MutableSet<Warehouse>
    )

    private fun expandForward(
        current: SearchState,
        opposite: SearchState
    ): Boolean {

        return expand(
            current = current,
            opposite = opposite,
            neighborsProvider = graph::neighbors
        )
    }

    private fun expandBackward(
        current: SearchState,
        opposite: SearchState
    ): Boolean {

        return expand(
            current = current,
            opposite = opposite,
            neighborsProvider = graph::predecessors
        )
    }

    private fun expand(
        current: SearchState,
        opposite: SearchState,
        neighborsProvider: (Warehouse) -> Map<Warehouse, Double>
    ): Boolean {

        val currentWarehouse =
            current.queue.removeFirst()

        for (neighbor in neighborsProvider(currentWarehouse).keys) {

            if (neighbor in opposite.visited) {
                return true
            }

            if (neighbor !in current.visited) {
                current.visited.add(neighbor)
                current.queue.addLast(neighbor)
            }
        }

        return false
    }
}