package org.bytebloom.data.migration

import io.github.jan.supabase.SupabaseClient
import org.bytebloom.data.remote.dto.WarehouseDto
import org.bytebloom.domain.model.Warehouse
import io.github.jan.supabase.postgrest.from

class SupabaseDataSeeder(
    private val supabase: SupabaseClient
) {

    suspend fun seedWarehouses(
        warehouses: List<Warehouse>
    ) {
        val data = warehouses.map { warehouse ->
            WarehouseDto(
                id = warehouse.id,
                name = warehouse.name,
                regionalZone = warehouse.regionalZone,
                latitude = warehouse.latitude.toFloat(),
                longitude = warehouse.longitude.toFloat()
            )
        }

        if (data.isNotEmpty()) {
            supabase
                .from("warehouses")
                .insert(data)
        }
    }
}