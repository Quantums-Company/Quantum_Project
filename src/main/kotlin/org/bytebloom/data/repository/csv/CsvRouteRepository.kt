package org.bytebloom.data.repository.csv

import org.bytebloom.data.mapper.RouteMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.source.csv.RouteDataSource
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CsvRouteRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvRouteDataSource: RouteDataSource
) : RouteRepository {
    private val mutex = Mutex()
    private var cachedRoutes: List<Route>? = null

    private suspend fun loadAll():List<Route>{
        val routeMapper = RouteMapper(WarehouseReferenceMapper(warehousesById))

        return routeMapper.toDomain(csvRouteDataSource.loadAll())
    }

    suspend fun refresh(){
        mutex.withLock { cachedRoutes = loadAll() }
    }

    override suspend fun getAll(): List<Route> =
        cachedRoutes ?:  mutex.withLock { cachedRoutes ?: loadAll().also { cachedRoutes = it }
    }

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