package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadRoutes
import org.bytebloom.data.mapper.RouteMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository

class CsvRouteRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : RouteRepository {

    override fun getAll(): List<Route> {
        val routeMapper = RouteMapper(WarehouseReferenceMapper(warehousesById))

        return routeMapper.toDomain(loadRoutes(csvDirectory))
    }
}