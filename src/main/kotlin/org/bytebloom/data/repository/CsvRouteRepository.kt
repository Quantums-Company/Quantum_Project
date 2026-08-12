package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadRoutes
import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.domain.repository.RouteRepository

class CsvRouteRepository(private val fileName: String = "routes.csv") : RouteRepository {
    override fun getAllRoutes(): List<RouteRaw> {
        return loadRoutes(fileName)
    }
}