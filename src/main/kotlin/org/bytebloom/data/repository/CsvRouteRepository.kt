package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadRoutes
import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository

class CsvRouteRepository : RouteRepository {
    override fun getAllRoutes(): List<Route> {
        return loadRoutes()
    }
}