package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadRoutes
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository

class CsvRouteRepository(
    private val warehouseMap: Map<String, Warehouse>
) : RouteRepository {
    override fun getAllRoutes(): List<Route> {
        return loadRoutes().mapNotNull { it.toDomain(warehouseMap) }
    }
}