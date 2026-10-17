package org.bytebloom.logiroute.domain.usecase.queries.routing

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.common.RouteFinder

class FindFewestHopsRouteUseCase(
    private val routeFinder: RouteFinder
) {

    operator fun invoke(
        startWarehouse: Warehouse,
        destinationWarehouse: Warehouse
    ): List<Warehouse>? =
        routeFinder.findShortestPath(
            startWarehouse,
            destinationWarehouse
        )
}