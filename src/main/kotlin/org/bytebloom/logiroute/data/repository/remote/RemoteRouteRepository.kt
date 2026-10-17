package org.bytebloom.logiroute.data.repository.remote

import org.bytebloom.logiroute.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.logiroute.data.remote.mapper.RouteDtoMapper
import org.bytebloom.logiroute.data.source.remote.RouteRemoteDataSource
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import java.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RemoteRouteRepository(
    private val warehouseRepository: WarehouseRepository,
    private val remoteDataSource: RouteRemoteDataSource
) : RouteRepository {

    private suspend fun <T> loadWithMapper(load: suspend () -> T): Pair<T, RouteDtoMapper> = coroutineScope {
        val result = async { load() }
        val warehouses = async { warehouseRepository.getAll() }
        result.await() to RouteDtoMapper(warehouses.await().associateBy { it.id } )
    }

    override suspend fun getAll(): List<Route> {
        val (dto, mapper) = loadWithMapper { remoteDataSource.loadAll() }
        return mapper.mapList(dto).succeeded }

    override suspend fun getById(id: String): Route? {
        val (dto, mapper) = loadWithMapper { remoteDataSource.loadById(id) }
            return dto?.let (mapper::map)
    }

    override suspend fun create(route: Route): Route {
        val (dto, mapper) = loadWithMapper { remoteDataSource.create(route.toRequestDto())}
        return dto?.let ( mapper::map)  ?: route
    }

    override suspend fun update(route: Route): Route {
        val (dto, mapper) = loadWithMapper { remoteDataSource.update(route.id, route.toRequestDto())}
        return dto?.let ( mapper::map) ?: route
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