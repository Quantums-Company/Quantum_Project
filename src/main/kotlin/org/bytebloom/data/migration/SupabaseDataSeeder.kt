package org.bytebloom.data.migration

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.data.remote.client.SupabaseHttpClient
import org.bytebloom.data.remote.mapper.DomainToRequestMapper
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Package

class SupabaseDataSeeder(
    private val supabase: SupabaseHttpClient
) {

    suspend fun seedWarehouses(
        warehouses: List<Warehouse>
    ) {
        val data = DomainToRequestMapper.toWarehouseRequests(warehouses)

        if (data.isNotEmpty()) {
            supabase.insert("warehouses", data)
        }
    }

    suspend fun seedVehicles(
        vehicles: List<Vehicle>
    ) {
        val data = DomainToRequestMapper.toVehicleRequests(vehicles)

        if (data.isNotEmpty()) {
            supabase.insert("vehicles", data)
        }
    }

    suspend fun seedRoutes(
        routes: List<Route>
    ) {
        val data = DomainToRequestMapper.toRouteRequests(routes)

        if (data.isNotEmpty()) {
            supabase.insert("routes", data)
        }
    }

    suspend fun seedPackages(
        packages: List<Package>
    ) {
        val data = DomainToRequestMapper.toPackageRequests(packages)

        if (data.isNotEmpty()) {
            supabase.insert("packages", data)
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