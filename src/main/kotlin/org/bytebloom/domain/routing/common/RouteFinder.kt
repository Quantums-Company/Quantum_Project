package org.bytebloom.domain.routing.common

import org.bytebloom.domain.model.Warehouse

interface RouteFinder {

    fun findShortestPath(
        start: Warehouse,
        destination: Warehouse
    ): List<Warehouse>?
}