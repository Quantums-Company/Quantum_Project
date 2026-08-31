package org.bytebloom.domain.routing.common

import org.bytebloom.domain.model.Warehouse

interface EmptyHubFinder {

    fun findNearestEmptyHub(
        start: Warehouse,
        emptyWarehouses: Set<Warehouse>
    ): Warehouse?
}