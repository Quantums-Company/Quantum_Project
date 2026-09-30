package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.mapper.RouteDtoMapper
import org.bytebloom.data.source.remote.RouteRemoteDataSource
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.WarehouseRepository
import java.time.Instant

class RemoteRouteRepository(
    private val warehouseRepository: WarehouseRepository,
    private val remoteDataSource: RouteRemoteDataSource
) : RouteRepository {

    private suspend fun mapper() = RouteDtoMapper(warehouseRepository.getAll().associateBy { it.id })

    override suspend fun getAll(): List<Route> =
        mapper().mapList(remoteDataSource.loadAll())

    override suspend fun getById(id: String): Route? =
        remoteDataSource.loadById(id)?.let {mapper().map(it) }

    override suspend fun create(route: Route): Route {
        val dto = remoteDataSource.create(route.toRequestDto())
        return dto?.let { mapper().map(it) } ?: route
    }

    override suspend fun update(route: Route): Route {
        val dto = remoteDataSource.update(route.id, route.toRequestDto())
        return dto?.let { mapper().map(it) } ?: route
    }

    override suspend fun delete(id: String): Boolean =
        remoteDataSource.delete(id)

    private fun Route.toRequestDto() = RouteRequestDto(
        id = id, originWarehouseId = originWarehouse.id,
        destinationWarehouseId = destinationWarehouse.id,
        distanceKm = distanceKm, typicalDelayMin = typicalDelayMin,
        updatedAt = Instant.now().toString()
    )
}