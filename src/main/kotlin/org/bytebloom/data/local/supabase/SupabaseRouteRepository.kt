package org.bytebloom.data.local.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto
import org.bytebloom.data.remote.mapper.RouteDtoMapper
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.WarehouseRepository

class SupabaseRouteRepository(
    private val client: SupabaseClient,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    private suspend fun warehousesById() =
        warehouseRepository.getAll().associateBy { it.id }

    override suspend fun getAll(): List<Route> {
        val dtos = client.from(TableName.ROUTES)
            .select()
            .decodeList<RouteResponseDto>()
        return RouteDtoMapper.toDomainList(dtos, warehousesById())
    }

    override suspend fun getById(id: String): Route? {
        val dto = client.from(TableName.ROUTES)
            .select { filter { eq("id", id) } }
            .decodeSingleOrNull<RouteResponseDto>()
        return dto?.let { RouteDtoMapper.toDomain(it, warehousesById()) }
    }

    override suspend fun create(route: Route): Route {
        val request = RouteRequestDto(
            id = route.id,
            originWarehouseId = route.originWarehouse.id,
            destinationWarehouseId = route.destinationWarehouse.id,
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin
        )
        client.from(TableName.ROUTES).insert(request)
        return route
    }

    override suspend fun update(route: Route): Route {
        val request = RouteRequestDto(
            id = route.id,
            originWarehouseId = route.originWarehouse.id,
            destinationWarehouseId = route.destinationWarehouse.id,
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin
        )
        client.from(TableName.ROUTES).update(request) {
            filter { eq("id", route.id) }
        }
        return route
    }

    override suspend fun delete(id: String): Boolean {
        client.from(TableName.ROUTES).delete { filter { eq("id", id) } }
        return true
    }
}