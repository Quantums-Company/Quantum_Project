package org.bytebloom.domain.usecase.queries.routing

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.common.RouteFinder

class VerifyHubLinkUseCase(
    private val routeFinder: RouteFinder
) {

    operator fun invoke(
        origin: Warehouse,
        destination: Warehouse
    ): Boolean =
        origin != destination && routeFinder.findShortestPath(origin, destination) != null
}