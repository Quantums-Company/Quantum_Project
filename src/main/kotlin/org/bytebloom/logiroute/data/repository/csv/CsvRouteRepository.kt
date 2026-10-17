package org.bytebloom.logiroute.data.repository.csv

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bytebloom.logiroute.data.mapper.RouteMapper
import org.bytebloom.logiroute.data.mapper.WarehouseReferenceMapper
import org.bytebloom.logiroute.data.source.csv.RouteDataSource
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

class CsvRouteRepository(
    private val warehouseRepository: WarehouseRepository,
    private val csvRouteDataSource: RouteDataSource
) : RouteRepository {

    private var cachedRoutes: List<Route>? = null
    private val mutex = Mutex()

    private suspend fun loadAll(): List<Route> {
        val warehousesById = warehouseRepository
            .getAll()
            .associateBy { it.id }

        val mapper = RouteMapper(
            WarehouseReferenceMapper(warehousesById)
        )

        return mapper.toDomain(csvRouteDataSource.loadAll())
    }

    suspend fun refresh(): List<Route> =
        mutex.withLock {
            loadAll().also { routes ->
                cachedRoutes = routes
            }
        }

    override suspend fun getAll(): List<Route> {
        cachedRoutes?.let { return it }

        return mutex.withLock {
            cachedRoutes
                ?: loadAll().also { routes ->
                    cachedRoutes = routes
                }
        }
    }

    override suspend fun getById(id: String): Route? =
        getAll().find { it.id == id }

    override suspend fun create(route: Route): Route =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Create the route through the remote repository."
        )

    override suspend fun update(route: Route): Route =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Update the route through the remote repository."
        )

    override suspend fun delete(id: String): Boolean =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Delete the route through the remote repository."
        )
}
