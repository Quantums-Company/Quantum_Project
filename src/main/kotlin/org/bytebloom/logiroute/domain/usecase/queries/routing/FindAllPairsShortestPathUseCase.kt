package org.bytebloom.logiroute.domain.usecase.queries.routing

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.common.RouteFinder

class FindAllPairsShortestPathUseCase(
    private val routeFinder: RouteFinder,
    private val graph: WarehouseGraph
) {

    operator fun invoke(): Map<Warehouse, Map<Warehouse, Double>> {
        val warehouses = graph.warehouses()

        return warehouses.associateWith { origin ->
            warehouses.associateWith { destination ->
                if (origin == destination) {
                    0.0
                } else {
                    shortestDistance(origin, destination)
                }
            }
        }
    }

    private fun shortestDistance(
        origin: Warehouse,
        destination: Warehouse
    ): Double {
        val path = routeFinder.findShortestPath(origin, destination)
            ?: return Double.POSITIVE_INFINITY

        return path
            .zipWithNext()
            .sumOf { (current, next) ->
                graph.neighbors(current)[next]
                    ?: Double.POSITIVE_INFINITY
            }
    }
}