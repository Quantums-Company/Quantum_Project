package org.bytebloom.domain.usecase.dispatch

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle

fun zonesCovered(vehicle: Vehicle, allRoutes: List<Route>): Set<String> {
    val directRouteZones = allRoutes
        .filter { route -> route.originWarehouse.id == vehicle.currentWarehouse.id }
        .map { route -> route.destinationWarehouse.regionalZone }

    return (sequenceOf(vehicle.currentWarehouse.regionalZone) + directRouteZones).toSet()
}