package org.bytebloom.logiroute.data.csv.seeding

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.logiroute.data.csv.datasource.CsvPackageDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvRouteDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvVehicleDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvWarehouseDataSource
import org.bytebloom.logiroute.data.csv.mapper.PackageMapper
import org.bytebloom.logiroute.data.csv.mapper.RouteMapper
import org.bytebloom.logiroute.data.csv.mapper.VehicleMapper
import org.bytebloom.logiroute.data.csv.mapper.WarehouseReferenceMapper
import org.bytebloom.logiroute.data.csv.mapper.toDomain
import org.bytebloom.logiroute.data.remote.datasource.TableName
import org.bytebloom.logiroute.data.remote.dto.PackageRequestDto
import org.bytebloom.logiroute.data.remote.dto.RouteRequestDto
import org.bytebloom.logiroute.data.remote.dto.VehicleRequestDto
import org.bytebloom.logiroute.data.remote.dto.WarehouseRequestDto
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import java.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CsvToSupabaseUploader(
    private val client: SupabaseClient,
    private val warehouseDataSource: CsvWarehouseDataSource,
    private val routeDataSource: CsvRouteDataSource,
    private val vehicleDataSource: CsvVehicleDataSource,
    private val packageDataSource: CsvPackageDataSource
) {

    suspend fun upload() = coroutineScope {
        val now = Instant.now().toString()

        val warehouses = warehouseDataSource.loadAll().toDomain()
        uploadWarehouses(warehouses, now)
        val warehouseReferenceMapper = WarehouseReferenceMapper(warehouses.associateBy { it.id })

        val routes = async {
            RouteMapper(warehouseReferenceMapper).toDomain(routeDataSource.loadAll())
                .also { uploadRoutes(it, now) }
        }
        val vehicles = async {
            VehicleMapper(warehouseReferenceMapper).toDomain(vehicleDataSource.loadAll())
                .also { uploadVehicles(it, now) }
        }
        val packages = async {
            PackageMapper(warehouseReferenceMapper).toDomain(packageDataSource.loadAll())
                .also { uploadPackages(it, now) }
        }

        println(
            "Upload complete: ${warehouses.size} warehouses, ${routes.await().size} routes, " +
                    "${vehicles.await().size} vehicles, ${packages.await().size} packages (synced at $now)"
        )
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
        client.from(TableName.WAREHOUSES.value).upsert(requests) { onConflict = "id" }
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
        client.from(TableName.ROUTES.value).upsert(requests) { onConflict = "id" }
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
        client.from(TableName.VEHICLES.value).upsert(requests) { onConflict = "id" }
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
        client.from(TableName.PACKAGES.value).upsert(requests) { onConflict = "id" }
    }
}