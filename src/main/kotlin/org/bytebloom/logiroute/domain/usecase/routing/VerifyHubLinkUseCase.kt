package org.bytebloom.logiroute.domain.usecase.routing

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.common.RouteFinder

class VerifyHubLinkUseCase(
    private val routeFinder: RouteFinder
) {

    operator fun invoke(
        origin: Warehouse,
        destination: Warehouse
    ): Boolean =
        origin != destination && routeFinder.findShortestPath(origin, destination) != null
}