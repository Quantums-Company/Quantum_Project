package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.mapper.RouteDtoMapper
import org.bytebloom.data.source.remote.RouteRemoteDataSource
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.util.retryWithBackoff
import java.time.Instant

class RemoteRouteRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val remoteDataSource: RouteRemoteDataSource
) : RouteRepository {

    override suspend fun getAll(): List<Route> {
        val dtos = retryWithBackoff { remoteDataSource.loadAll() }.getOrThrow()
        return RouteDtoMapper.toDomainList(dtos, warehousesById)
    }

    override suspend fun getById(id: String): Route? {
        val dto = retryWithBackoff { remoteDataSource.loadById(id) }.getOrThrow()
        return dto?.let { RouteDtoMapper.toDomain(it, warehousesById) }
    }

    override suspend fun create(route: Route): Route {
        val dto = retryWithBackoff { remoteDataSource.create(route.toRequestDto()) }.getOrThrow()
        return dto?.let { RouteDtoMapper.toDomain(it, warehousesById) } ?: route
    }

    override suspend fun update(route: Route): Route {
        val dto = retryWithBackoff { remoteDataSource.update(route.id, route.toRequestDto()) }.getOrThrow()
        return dto?.let { RouteDtoMapper.toDomain(it, warehousesById) } ?: route
    }

    override suspend fun delete(id: String): Boolean {
        return retryWithBackoff { remoteDataSource.delete(id) }.getOrThrow()
    }

    private fun Route.toRequestDto() = RouteRequestDto(
        id = id, originWarehouseId = originWarehouse.id,
        destinationWarehouseId = destinationWarehouse.id,
        distanceKm = distanceKm, typicalDelayMin = typicalDelayMin,
        updatedAt = Instant.now().toString()
    )
}