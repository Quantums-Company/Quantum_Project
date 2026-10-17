package org.bytebloom.logiroute.data.local.cleaner

import org.bytebloom.logiroute.data.local.csv.CsvPackageDataSource
import org.bytebloom.logiroute.data.local.csv.CsvRouteDataSource
import org.bytebloom.logiroute.data.local.csv.CsvVehicleDataSource
import org.bytebloom.logiroute.data.local.csv.CsvWarehouseDataSource
import org.bytebloom.logiroute.data.mapper.PackageMapper
import org.bytebloom.logiroute.data.mapper.RouteMapper
import org.bytebloom.logiroute.data.mapper.VehicleMapper
import org.bytebloom.logiroute.data.mapper.WarehouseReferenceMapper
import org.bytebloom.logiroute.data.mapper.toDomain
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import java.io.File

class CsvDataCleaner(
    private val warehouseDataSource: CsvWarehouseDataSource,
    private val routeDataSource: CsvRouteDataSource,
    private val vehicleDataSource: CsvVehicleDataSource,
    private val packageDataSource: CsvPackageDataSource,
    private val csvWriter: CsvWriter
) {

    companion object {
        private const val CLEANED_DIRECTORY = "src/resources/cleaned"

        private const val WAREHOUSES_FILE = "warehouses.csv"
        private const val ROUTES_FILE = "routes.csv"
        private const val VEHICLES_FILE = "vehicles.csv"
        private const val PACKAGES_FILE = "packages.csv"
    }

    suspend fun clean() {
        val warehouses = loadValidWarehouses()
        val warehouseReferenceMapper = WarehouseReferenceMapper(warehouses.associateBy { it.id })

        writeWarehouses(warehouses)
        writeRoutes(loadRoutesWithValidWarehouses(warehouseReferenceMapper))
        writeVehicles(loadVehiclesWithValidWarehouse(warehouseReferenceMapper))
        writePackages(loadPackagesWithValidWarehouses(warehouseReferenceMapper))
    }

    private suspend fun loadValidWarehouses(): List<Warehouse> =
        warehouseDataSource.loadAll().toDomain()

    private suspend fun loadRoutesWithValidWarehouses(mapper: WarehouseReferenceMapper): List<Route> =
        RouteMapper(warehouseMapper = mapper).toDomain(routeDataSource.loadAll())

    private suspend fun loadVehiclesWithValidWarehouse(mapper: WarehouseReferenceMapper): List<Vehicle> =
        VehicleMapper(warehouseMapper = mapper).toDomain(vehicleDataSource.loadAll())

    private suspend fun loadPackagesWithValidWarehouses(mapper: WarehouseReferenceMapper): List<Package> =
        PackageMapper(warehouseMapper = mapper).toDomain(packageDataSource.loadAll())

    private fun writeWarehouses(
        warehouses: List<Warehouse>
    ) {
        val rows = warehouses.map { warehouse ->
            listOf(
                warehouse.id,
                warehouse.name,
                warehouse.regionalZone,
                warehouse.latitude.toString(),
                warehouse.longitude.toString()
            ).joinToCsv()
        }

        csvWriter.write(
            file = File(
                CLEANED_DIRECTORY,
                WAREHOUSES_FILE
            ),
            header = "id,name,regional_zone,latitude,longitude",
            rows = rows
        )
    }

    private fun writeRoutes(
        routes: List<Route>
    ) {
        val rows = routes.map { route ->
            listOf(
                route.id,
                route.originWarehouse.id,
                route.destinationWarehouse.id,
                route.distanceKm.toString(),
                route.typicalDelayMin.toString()
            ).joinToCsv()
        }

        csvWriter.write(
            file = File(
                CLEANED_DIRECTORY,
                ROUTES_FILE
            ),
            header = "id,origin_warehouse_id,destination_warehouse_id,distance_km,typical_delay_min",
            rows = rows
        )
    }

    private fun writeVehicles(
        vehicles: List<Vehicle>
    ) {
        val rows = vehicles.map { vehicle ->
            listOf(
                vehicle.id,
                vehicle.currentWarehouse.id,
                vehicle.maxCapacityKg.toString(),
                vehicle.costPerKm.toString()
            ).joinToCsv()
        }

        csvWriter.write(
            file = File(
                CLEANED_DIRECTORY,
                VEHICLES_FILE
            ),
            header = "id,current_warehouse_id,max_capacity_kg,cost_per_km",
            rows = rows
        )
    }

    private fun writePackages(
        packages: List<Package>
    ) {
        val rows = packages.map { packageData ->
            listOf(
                packageData.id,
                packageData.weight.toString(),
                packageData.originWarehouse.id,
                packageData.destinationWarehouse.id,
                packageData.priority.name
            ).joinToCsv()
        }

        csvWriter.write(
            file = File(
                CLEANED_DIRECTORY,
                PACKAGES_FILE
            ),
            header = "id,weight,origin_warehouse_id,destination_warehouse_id,priority",
            rows = rows
        )
    }

    private fun List<String>.joinToCsv(): String =
        joinToString(",") { value ->
            escapeCsvValue(value)
        }

    private fun escapeCsvValue(value: String): String {
        return if (
            value.contains(",") ||
            value.contains("\"") ||
            value.contains("\n")
        ) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}