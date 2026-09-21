package org.bytebloom.data.local.cleaner

import org.bytebloom.data.local.csv.CsvPackageDataSource
import org.bytebloom.data.local.csv.CsvRouteDataSource
import org.bytebloom.data.local.csv.CsvVehicleDataSource
import org.bytebloom.data.local.csv.CsvWarehouseDataSource
import org.bytebloom.data.mapper.PackageMapper
import org.bytebloom.data.mapper.RouteMapper
import org.bytebloom.data.mapper.VehicleMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
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

        /*
         * 1. Read and validate warehouses.
         *
         * Warehouse does not depend on another entity,
         * so it must be processed first.
         */
        val warehouses = warehouseDataSource
            .loadAll()
            .toDomain()

        val warehousesById = warehouses.associateBy { it.id }

        /*
         * 2. Create the reference mapper.
         *
         * Routes, vehicles and packages depend on warehouses.
         */
        val warehouseReferenceMapper =
            WarehouseReferenceMapper(warehousesById)

        /*
         * 3. Read and validate routes.
         *
         * RouteMapper removes routes whose warehouse references
         * are not valid.
         */
        val routes = RouteMapper(
            warehouseMapper = warehouseReferenceMapper
        ).toDomain(
            routeDataSource.loadAll()
        )

        /*
         * 4. Read and validate vehicles.
         *
         * VehicleMapper removes vehicles whose current warehouse
         * does not exist.
         */
        val vehicles = VehicleMapper(
            warehouseMapper = warehouseReferenceMapper
        ).toDomain(
            vehicleDataSource.loadAll()
        )

        /*
         * 5. Read and validate packages.
         *
         * PackageMapper removes packages whose origin or
         * destination warehouse does not exist.
         */
        val packages = PackageMapper(
            warehouseMapper = warehouseReferenceMapper
        ).toDomain(
            packageDataSource.loadAll()
        )

        /*
         * 6. Write the clean data.
         */
        writeWarehouses(warehouses)
        writeRoutes(routes)
        writeVehicles(vehicles)
        writePackages(packages)
    }

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