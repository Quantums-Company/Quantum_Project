package org.bytebloom.domain.routing.bfs

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.routing.common.RouterValidator

class BidirectionalBreadthFirstRouter(
    private val graph: WarehouseGraph
) : RouterValidator(graph), RouteFinder {

    var evaluatedWarehouses = 0

    private class SearchState(start: Warehouse){
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

        val forward = SearchState(start)
        val backward = SearchState(destination)

        val meetingPoint = search(forward, backward)?: return null

        return reconstructPath(
                meetingPoint = meetingPoint,
                forwardParent = forward.parent,
                backwardParent = backward.parent
            )

    }

    private fun search(forward: SearchState, backward: SearchState): Warehouse? {
        while (forward.queue.isNotEmpty() && backward.queue.isNotEmpty()) {

            expandLevel(forward, backward.visited, direction = Direction.FORWARD)
                ?.let { return it }

            expandLevel(backward, forward.visited, direction = Direction.BACKWARD)
                ?.let { return it }
        }

        return null
    }

    private enum class Direction { FORWARD, BACKWARD }

    private fun expandLevel(
        state: SearchState,
        otherVisited: Set<Warehouse>,
        direction: Direction
    ): Warehouse? {

        val levelSize = state.queue.size

        repeat(levelSize) {
            val current = state.queue.removeFirst()
            evaluatedWarehouses++

            val adjacent = when (direction) {
                Direction.FORWARD -> graph.neighbors(current).keys
                Direction.BACKWARD -> graph.predecessors(current).keys
            }

            for (next in adjacent) {
                if (next in state.visited) continue

                state.visited.add(next)
                state.parent[next] = current
                state.queue.addLast(next)

                if (next in otherVisited) return next
            }
        }

        return null
    }

    private fun reconstructPath(
        meetingPoint: Warehouse,
        forwardParent: Map<Warehouse, Warehouse>,
        backwardParent: Map<Warehouse, Warehouse>
    ): List<Warehouse> {

        val forwardHalf =
            generateSequence(meetingPoint) { forwardParent[it] }
                .toList()
                .asReversed()

        val backwardHalf =
            generateSequence(backwardParent[meetingPoint]) { backwardParent[it] }
                .toList()

        return forwardHalf + backwardHalf
    }
}