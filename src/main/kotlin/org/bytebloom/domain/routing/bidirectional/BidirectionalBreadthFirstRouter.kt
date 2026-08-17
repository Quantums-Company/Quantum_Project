package org.bytebloom.domain.routing.bidirectional

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.bfs.UnidirectionalBreadthFirstRouter
import org.bytebloom.domain.routing.common.RouterValidator

class BidirectionalBreadthFirstRouter(
    private val graph: WarehouseGraph
) : RouterValidator(graph) {

    private val fallbackRouter =
        UnidirectionalBreadthFirstRouter(graph)

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

        val forwardState =
            createSearchState(startWarehouse)

        val backwardState =
            createSearchState(endWarehouse)

        val meetingPoint = search(
            forwardState = forwardState,
            backwardState = backwardState
        )

        return if (meetingPoint != null) {
            reconstructBidirectionalPath(
                meetingPoint = meetingPoint,
                forwardParent = forwardState.parent,
                backwardParent = backwardState.parent
            )
        } else {
            fallbackRouter.findShortestPath(
                startWarehouse,
                endWarehouse
            )
        }
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
        forwardState: SearchState,
        backwardState: SearchState
    ): Warehouse? {

        while (
            forwardState.queue.isNotEmpty() &&
            backwardState.queue.isNotEmpty()
        ) {

            val forwardMeetingPoint =
                expandForward(
                    forwardState,
                    backwardState.visited
                )

            if (forwardMeetingPoint != null) {
                return forwardMeetingPoint
            }

            val backwardMeetingPoint =
                expandBackward(
                    backwardState,
                    forwardState.visited
                )

            if (backwardMeetingPoint != null) {
                return backwardMeetingPoint
            }
        }

        return null
    }

    private fun expandForward(
        state: SearchState,
        backwardVisited: Set<Warehouse>
    ): Warehouse? {

        val levelSize = state.queue.size

        repeat(levelSize) {

            val current = state.queue.removeFirst()

            for (neighbor in graph.neighbors(current).keys) {

                if(neighbor in state.visited){
                    continue
                }

                state.visited.add(neighbor)
                state.parent[neighbor] = current
                state.queue.addLast(neighbor)

                if (neighbor in backwardVisited) {
                    return neighbor
                }
            }
        }

        return null
    }

    private fun expandBackward(
        state: SearchState,
        forwardVisited: Set<Warehouse>
    ): Warehouse? {

        val levelSize = state.queue.size

        repeat(levelSize) {

            val current = state.queue.removeFirst()

            for (neighbor in graph.predecessors(current).keys) {

                if (neighbor in state.visited) {
                    continue
                }

                state.visited.add(neighbor)
                state.parent[neighbor] = current
                state.queue.addLast(neighbor)

                if (neighbor in forwardVisited) {
                    return neighbor
                }
            }
        }

        return null
    }

    private fun reconstructBidirectionalPath(
        meetingPoint: Warehouse,
        forwardParent: Map<Warehouse, Warehouse>,
        backwardParent: Map<Warehouse, Warehouse>
    ): List<Warehouse> {

        val forwardPath = mutableListOf<Warehouse>()

        var current: Warehouse? = meetingPoint

        while (current != null) {
            forwardPath.add(current)
            current = forwardParent[current]
        }

        forwardPath.reverse()

        val backwardPath = mutableListOf<Warehouse>()

        current = backwardParent[meetingPoint]

        while (current != null) {
            backwardPath.add(current)
            current = backwardParent[current]
        }

        return forwardPath + backwardPath
    }
}