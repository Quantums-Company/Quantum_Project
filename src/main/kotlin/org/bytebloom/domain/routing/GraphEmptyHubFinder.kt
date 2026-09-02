package org.bytebloom.domain.routing

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.routing.common.EmptyHubFinder

class GraphEmptyHubFinder(
    private val warehouseRepository: WarehouseRepository,
    private val routeRepository: RouteRepository
) : EmptyHubFinder {

    private val adjacencyList: Map<String, List<String>> by lazy { buildAdjacencyList() }
    private val warehouseMap: Map<String, Warehouse> by lazy { warehouseRepository.getAll().associateBy { it.id } }

    override fun findNearestEmptyHub(
        start: Warehouse,
        emptyWarehouses: Set<Warehouse>
    ): Warehouse? {
        val emptyIds = emptyWarehouses.map { it.id }.toSet()
        val startId = start.id

        if (isAlreadyAtEmptyHub(startId, emptyIds)) return start

        return performBidirectionalSearch(startId, emptyIds)
    }

    private fun isAlreadyAtEmptyHub(startId: String, emptyIds: Set<String>): Boolean {
        return startId in emptyIds
    }

    private fun performBidirectionalSearch(startId: String, emptyIds: Set<String>): Warehouse? {
        var forwardFrontier = listOf(startId)
        var backwardFrontier = emptyIds.toList()

        var forwardVisited = setOf(startId)
        var backwardVisited = emptyIds

        while (hasValidFrontiers(forwardFrontier, backwardFrontier)) {
            val nextForward = expandFrontier(forwardFrontier, forwardVisited)
            val nextBackward = expandFrontier(backwardFrontier, backwardVisited)

            findIntersection(nextForward, nextBackward, forwardVisited, backwardVisited)?.let { intersectingId ->
                return warehouseMap[intersectingId]
            }

            if (areFrontiersStuck(nextForward, nextBackward)) break

            forwardFrontier = nextForward
            backwardFrontier = nextBackward
            forwardVisited = forwardVisited union nextForward
            backwardVisited = backwardVisited union nextBackward
        }

        return null
    }

    private fun buildAdjacencyList(): Map<String, List<String>> {
        return routeRepository.getAll()
            .groupBy { it.originWarehouse.id }
            .mapValues { (_, routes) -> routes.map { it.destinationWarehouse.id } }
    }

    private fun expandFrontier(currentFrontier: List<String>, visitedNodes: Set<String>): List<String> {
        return currentFrontier
            .flatMap { nodeId -> adjacencyList[nodeId].orEmpty() }
            .filter { neighborId -> neighborId !in visitedNodes }
            .distinct()
    }

    private fun findIntersection(
        nextForward: List<String>,
        nextBackward: List<String>,
        forwardVisited: Set<String>,
        backwardVisited: Set<String>
    ): String? {
        val forwardSet = nextForward.toSet()
        val backwardSet = nextBackward.toSet()

        val intersection = (forwardSet intersect backwardVisited) +
                (backwardSet intersect forwardVisited) +
                (forwardSet intersect backwardSet)

        return intersection.firstOrNull()
    }

    private fun hasValidFrontiers(forward: List<String>, backward: List<String>): Boolean {
        return forward.isNotEmpty() && backward.isNotEmpty()
    }

    private fun areFrontiersStuck(nextForward: List<String>, nextBackward: List<String>): Boolean {
        return nextForward.isEmpty() || nextBackward.isEmpty()
    }
}