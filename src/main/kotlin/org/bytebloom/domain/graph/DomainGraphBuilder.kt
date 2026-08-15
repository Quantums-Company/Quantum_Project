package org.bytebloom.domain.graph

import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.data.raw.VehicleRaw
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.util.Logger

private const val VEHICLE = "Vehicle"
private const val PACKAGE = "Package"
private const val ROUTE = "Route"

object DomainGraphBuilder {

    private fun findWarehouse(
        warehouseMap: Map<String, Warehouse>,
        warehouseId: String,
        owner: String
    ): Warehouse? {

        val warehouse = warehouseMap[warehouseId]

        if (warehouse == null) {
            Logger.warning(
                "$owner references unknown warehouse '$warehouseId'."
            )
        }

        return warehouse
    }

    private fun buildWarehouses(
        warehouseRaws: List<WarehouseRaw>
    ): Map<String, Warehouse> {

        return warehouseRaws.associateBy(
            keySelector = { it.id },
            valueTransform = { raw ->
                Warehouse(
                    raw.id,
                    raw.name,
                    raw.regionalZone,
                    raw.longitude,
                    raw.latitude,
                )
            }
        )
    }

    private fun buildVehicles(
        vehicleRaws: List<VehicleRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Vehicle> {
        return vehicleRaws.mapNotNull { raw ->
            val warehouse = findWarehouse(
                warehouseMap = warehouseMap,
                warehouseId = raw.currentWarehouseId,
                owner = VEHICLE
            ) ?: return@mapNotNull null

            val vehicle = Vehicle(
                raw.id,
                raw.maxCapacityKg,
                raw.costPerKm,
                warehouse
            )

            warehouse.addVehicle(vehicle)
            vehicle
        }
    }

    private fun buildPackages(
        packageRaws: List<PackageRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Package> {
        return packageRaws.mapNotNull { raw ->
            val origin = findWarehouse(
                warehouseMap,
                raw.originWarehouseId,
                PACKAGE)
            val destination = findWarehouse(
                warehouseMap,
                raw.destinationWarehouseId,
                PACKAGE)

            if (origin == null || destination == null) {
                return@mapNotNull null
            }
            val pkg = Package(
                raw.id,
                raw.weight,
                raw.priority,
                origin,
                destination).
                also(origin::addPackage)
            pkg
        }
    }

    private fun buildRoutes(
        routeRaws: List<RouteRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Route> {
        return routeRaws.mapNotNull { raw ->
            val origin = findWarehouse(
                warehouseMap,
                raw.originWarehouseId,
                ROUTE)
            val destination = findWarehouse(
                warehouseMap,
                raw.destinationWarehouseId,
                ROUTE)

            if (origin == null || destination == null) {
                return@mapNotNull null
            }
            val route = Route(
                raw.id,
                raw.distanceKm,
                raw.typicalDelayMin,
                origin,
                destination).
                also(origin::addRoute)
            route
        }
    }

    fun buildGraph(
    warehouseRepository: WarehouseRepository,
    packageRepository: PackageRepository,
    routeRepository: RouteRepository,
    vehicleRepository: VehicleRepository
    ): DomainGraph {
    val warehouseRaws =
        warehouseRepository.getAllWarehouses()

    val packageRaws =
        packageRepository.getAllPackages()

    val routeRaws =
        routeRepository.getAllRoutes()

    val vehicleRaws =
        vehicleRepository.getAllVehicles()

    val warehouseMap =
        buildWarehouses(warehouseRaws)

    val vehicles =
        buildVehicles(vehicleRaws, warehouseMap)

    val packages =
        buildPackages(packageRaws, warehouseMap)

    val routes =
        buildRoutes(routeRaws, warehouseMap)

        return DomainGraph(
            warehouses = warehouseMap.values.toList(),
            packages = packages,
            routes = routes,
            vehicles = vehicles
        )
    }
}
