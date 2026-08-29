package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadPackages
import org.bytebloom.data.csv.loadRoutes
import org.bytebloom.data.mapper.PackageMapper
import org.bytebloom.data.mapper.RouteMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.util.Logger

class CsvRouteRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : RouteRepository {
    private var achedRoutes= listOf<Route>()

    private fun loadAll():List<Route>{
        val routeMapper = RouteMapper(WarehouseReferenceMapper(warehousesById))
        val routeRaws = loadRoutes(csvDirectory)

        return routeMapper.toDomain(routeRaws)
    }

    fun refresh(){
        achedRoutes = loadAll()
    }

    init {
        Logger.info("Loading routes in init...")
        achedRoutes = loadAll()
    }

    override fun getAll(): List<Route> = achedRoutes
}