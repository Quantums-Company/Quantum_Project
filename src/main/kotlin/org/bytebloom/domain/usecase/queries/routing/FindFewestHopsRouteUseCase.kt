package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.common.RouteFinder

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