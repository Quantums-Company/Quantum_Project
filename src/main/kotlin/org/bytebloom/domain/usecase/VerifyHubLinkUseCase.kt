package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.BidirectionalBFSConnectivityChecker

class VerifyHubLinkUseCase(
    private val connectivityChecker: BidirectionalBFSConnectivityChecker
) {

    operator fun invoke(
        origin: Warehouse,
        destination: Warehouse
    ): Boolean =
        connectivityChecker.isReachable(
            origin = origin,
            destination = destination
        )
}