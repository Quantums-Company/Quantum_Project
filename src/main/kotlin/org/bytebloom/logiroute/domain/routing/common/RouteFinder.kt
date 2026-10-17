package org.bytebloom.logiroute.domain.routing.common

import org.bytebloom.logiroute.domain.model.Warehouse

interface RouteFinder {

    fun findShortestPath(
        start: Warehouse,
        destination: Warehouse
    ): List<Warehouse>?
}