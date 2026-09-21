package org.bytebloom.data.migration

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.data.remote.mapper.DomainToRequestMapper
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Package
import org.bytebloom.data.remote.TableName
import org.bytebloom.util.retryWithBackoff

class SupabaseDataSeeder(
    private val supabase: SupabaseClient
) {

    suspend fun seedWarehouses(
        warehouses: List<Warehouse>
    ) {
        val data = DomainToRequestMapper.toWarehouseRequests(warehouses)

        if (data.isNotEmpty()) {
            retryWithBackoff {
                supabase.from(TableName.WAREHOUSES).insert(data)
            }.getOrThrow()
        }
    }

    suspend fun seedVehicles(
        vehicles: List<Vehicle>
    ) {
        val data = DomainToRequestMapper.toVehicleRequests(vehicles)

        if (data.isNotEmpty()) {
            retryWithBackoff {
                supabase.from(TableName.VEHICLES).insert(data)
            }.getOrThrow()
        }
    }

    suspend fun seedRoutes(
        routes: List<Route>
    ) {
        val data = DomainToRequestMapper.toRouteRequests(routes)

        if (data.isNotEmpty()) {
            retryWithBackoff {
                supabase.from(TableName.ROUTES).insert(data)
            }.getOrThrow()
        }
    }

    suspend fun seedPackages(
        packages: List<Package>
    ) {
        val data = DomainToRequestMapper.toPackageRequests(packages)

        if (data.isNotEmpty()) {
            retryWithBackoff {
                supabase.from(TableName.PACKAGES).insert(data)
            }.getOrThrow()
        }
    }

    suspend fun seedAll(
        warehouses: List<Warehouse>,
        vehicles: List<Vehicle>,
        routes: List<Route>,
        packages: List<Package>
    ) {
        seedWarehouses(warehouses)
        seedVehicles(vehicles)
        seedRoutes(routes)
        seedPackages(packages)
    }
}