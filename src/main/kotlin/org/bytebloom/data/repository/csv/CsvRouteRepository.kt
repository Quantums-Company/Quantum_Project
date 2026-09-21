package org.bytebloom.data.repository.csv

import org.bytebloom.data.mapper.RouteMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.source.csv.RouteDataSource
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CsvRouteRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvRouteDataSource: RouteDataSource
) : RouteRepository {
    private var cachedRoutes= listOf<Route>()

    private suspend fun loadAll():List<Route>{
        val routeMapper = RouteMapper(WarehouseReferenceMapper(warehousesById))
        val routeRaws = csvRouteDataSource.loadAll()

        return routeMapper.toDomain(routeRaws)
    }

    suspend fun refresh(){
        cachedRoutes = loadAll()
    }

    init {
        Logger.info("Loading routes in init...")
        CoroutineScope(Dispatchers.IO).launch {
            refresh()
        }
    }

    override suspend fun getAll(): List<Route> = cachedRoutes
    override suspend fun getById(id: String): Route? {
        TODO("Not yet implemented")
    }

    override suspend fun create(route: Route): Route {
        TODO("Not yet implemented")
    }

    override suspend fun update(route: Route): Route {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}