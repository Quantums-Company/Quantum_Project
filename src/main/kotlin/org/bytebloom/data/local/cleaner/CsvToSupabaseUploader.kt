package org.bytebloom.data.local.cleaner

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.local.CsvPackageDataSource
import org.bytebloom.data.local.CsvRouteDataSource
import org.bytebloom.data.local.CsvVehicleDataSource
import org.bytebloom.data.local.CsvWarehouseDataSource
import org.bytebloom.data.mapper.PackageMapper
import org.bytebloom.data.mapper.RouteMapper
import org.bytebloom.data.mapper.VehicleMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.data.remote.TableName
import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseRequestDto
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import java.time.Instant

class CsvToSupabaseUploader(
    private val client: SupabaseClient,
    private val warehouseDataSource: CsvWarehouseDataSource,
    private val routeDataSource: CsvRouteDataSource,
    private val vehicleDataSource: CsvVehicleDataSource,
    private val packageDataSource: CsvPackageDataSource
) {

    suspend fun upload() {
        val now = Instant.now().toString()

        // 1. Warehouses first — everything else depends on them.
        val warehouses = warehouseDataSource.loadAll().toDomain()
        val warehousesById = warehouses.associateBy { it.id }
        uploadWarehouses(warehouses, now)

        val warehouseReferenceMapper = WarehouseReferenceMapper(warehousesById)

        // 2. Routes, vehicles, packages — depend on warehouses existing above.
        val routes = RouteMapper(warehouseReferenceMapper).toDomain(routeDataSource.loadAll())
        uploadRoutes(routes, now)

        val vehicles = VehicleMapper(warehouseReferenceMapper).toDomain(vehicleDataSource.loadAll())
        uploadVehicles(vehicles, now)

        val packages = PackageMapper(warehouseReferenceMapper).toDomain(packageDataSource.loadAll())
        uploadPackages(packages, now)

        println("Upload complete: ${warehouses.size} warehouses, ${routes.size} routes, " +
                "${vehicles.size} vehicles, ${packages.size} packages (synced at $now)")
    }

    private suspend fun uploadWarehouses(warehouses: List<Warehouse>, syncedAt: String) {
        if (warehouses.isEmpty()) return
        val requests = warehouses.map { warehouse ->
            WarehouseRequestDto(
                id = warehouse.id,
                name = warehouse.name,
                regionalZone = warehouse.regionalZone,
                longitude = warehouse.longitude,
                latitude = warehouse.latitude,
                updatedAt = syncedAt
            )
        }
        client.from(TableName.WAREHOUSES).upsert(requests) { onConflict = "id" }
    }

    private suspend fun uploadRoutes(routes: List<Route>, syncedAt: String) {
        if (routes.isEmpty()) return
        val requests = routes.map { route ->
            RouteRequestDto(
                id = route.id,
                originWarehouseId = route.originWarehouse.id,
                destinationWarehouseId = route.destinationWarehouse.id,
                distanceKm = route.distanceKm,
                typicalDelayMin = route.typicalDelayMin,
                updatedAt = syncedAt
            )
        }
        client.from(TableName.ROUTES).upsert(requests) { onConflict = "id" }
    }

    private suspend fun uploadVehicles(vehicles: List<Vehicle>, syncedAt: String) {
        if (vehicles.isEmpty()) return
        val requests = vehicles.map { vehicle ->
            VehicleRequestDto(
                id = vehicle.id,
                currentWarehouseId = vehicle.currentWarehouse.id,
                maxCapacityKg = vehicle.maxCapacityKg,
                costPerKm = vehicle.costPerKm,
                updatedAt = syncedAt
            )
        }
        client.from(TableName.VEHICLES).upsert(requests) { onConflict = "id" }
    }

    private suspend fun uploadPackages(packages: List<Package>, syncedAt: String) {
        if (packages.isEmpty()) return
        val requests = packages.map { pkg ->
            PackageRequestDto(
                id = pkg.id,
                weight = pkg.weight,
                originWarehouseId = pkg.originWarehouse.id,
                destinationWarehouseId = pkg.destinationWarehouse.id,
                priority = pkg.priority.name,
                updatedAt = syncedAt
            )
        }
        client.from(TableName.PACKAGES).upsert(requests) { onConflict = "id" }
    }
}