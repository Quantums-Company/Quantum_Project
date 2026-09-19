package org.bytebloom.data.local.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto
import org.bytebloom.data.remote.mapper.RouteDtoMapper
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.data.remote.client.SupabaseErrorTranslator

class SupabaseRouteRepository(
    private val client: SupabaseClient,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    private suspend fun warehousesById() =
        warehouseRepository.getAll().associateBy { it.id }

    override suspend fun getAll(): List<Route> =
        SupabaseErrorTranslator.translate("getAll routes") {
            val dtos = client.from(TableName.ROUTES)
                .select()
                .decodeList<RouteResponseDto>()
            RouteDtoMapper.toDomainList(dtos, warehousesById())
        }

    override suspend fun getById(id: String): Route? =
        SupabaseErrorTranslator.translate("getById route '$id'") {
            val dto = client.from(TableName.ROUTES)
                .select { filter { eq("id", id) } }
                .decodeSingleOrNull<RouteResponseDto>()
            dto?.let { RouteDtoMapper.toDomain(it, warehousesById()) }
        }

    override suspend fun create(route: Route): Route =
        SupabaseErrorTranslator.translate("create route '${route.id}'") {
            val request = RouteRequestDto(
                id = route.id,
                originWarehouseId = route.originWarehouse.id,
                destinationWarehouseId = route.destinationWarehouse.id,
                distanceKm = route.distanceKm,
                typicalDelayMin = route.typicalDelayMin
            )
            client.from(TableName.ROUTES).insert(request)
            route
        }

    override suspend fun update(route: Route): Route =
        SupabaseErrorTranslator.translate("update route '${route.id}'") {
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
            route
        }

    override suspend fun delete(id: String): Boolean =
        SupabaseErrorTranslator.translate("delete route '$id'") {
            client.from(TableName.ROUTES).delete { filter { eq("id", id) } }
            true
        }
}