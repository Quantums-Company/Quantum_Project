package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto
import org.bytebloom.data.remote.mapper.EntityMapper
import org.bytebloom.data.remote.mapper.RouteDtoMapper
import org.bytebloom.data.source.remote.RouteRemoteDataSource
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository
import java.time.Instant

class RemoteRouteRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val remoteDataSource: RouteRemoteDataSource
) : RouteRepository {

    val routeDtoMapper = RouteDtoMapper(warehousesById)

    override suspend fun getAll(): List<Route> =
        routeDtoMapper.mapList(remoteDataSource.loadAll())

    override suspend fun getById(id: String): Route? =
        remoteDataSource.loadById(id)?.let {routeDtoMapper.map(it) }

    override suspend fun create(route: Route): Route {
        val dto = remoteDataSource.create(route.toRequestDto())
        return dto?.let { routeDtoMapper.map(it) } ?: route
    }

    override suspend fun update(route: Route): Route {
        val dto = remoteDataSource.update(route.id, route.toRequestDto())
        return dto?.let { routeDtoMapper.map(it) } ?: route
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