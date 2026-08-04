package org.bytebloom.domain.logic

import org.bytebloom.data.dataHolder.PackageRaw
import org.bytebloom.data.dataHolder.RouteRaw
import org.bytebloom.data.dataHolder.VehicleRaw
import org.bytebloom.data.dataHolder.WarehouseRaw
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

data class DomainGraph(
    val warehouses: List<Warehouse>,
    val packages: List<Package>,
    val routes: List<Route>,
    val vehicles: List<Vehicle>
)

object DomainGraphBuilder {

    private fun findWarehouse(
        warehouseMap: Map<String, Warehouse>,
        warehouseId: String,
        objectType: String
    ): Warehouse? {

        val warehouse = warehouseMap[warehouseId]

        if (warehouse == null) {
            println(
                "Warning: $objectType references unknown warehouse '$warehouseId'."
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

        val vehiclesByHub = vehicleRaws.groupBy { it.currentWarehouseId }

        return vehiclesByHub.flatMap { (hubId, rawsForHub) ->

            val hub = findWarehouse(warehouseMap = warehouseMap, warehouseId = hubId, objectType = "Vehicle")
                ?: return@flatMap emptyList()

            rawsForHub.map { raw ->
                val vehicle = Vehicle(
                    raw.id,
                    raw.maxCapacityKg,
                    raw.costPerKm,
                    hub
                )

                hub.addVehicle(vehicle)
                vehicle
            }
        }
    }

    private fun buildPackages(
        packageRaws: List<PackageRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Package> {
        return packageRaws.mapNotNull { raw ->
            val origin = findWarehouse(warehouseMap, raw.originWarehouseId, "Package")
            val destination = findWarehouse(warehouseMap, raw.destinationWarehouseId, "Package")

            if (origin == null || destination == null) {
                return@mapNotNull null
            }
            val pkg = Package(raw.id, raw.weight, raw.priority, origin, destination)

            origin.addPackage(pkg)
            pkg
        }
    }

    private fun buildRoutes(
        routeRaws: List<RouteRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Route> {
        return routeRaws.mapNotNull { raw ->
            val origin = findWarehouse(warehouseMap, raw.originWarehouseId, "Route")
            val destination = findWarehouse(warehouseMap, raw.destinationWarehouseId, "Route")

            if (origin == null || destination == null) {
                return@mapNotNull null
            }
            val route = Route(raw.id, raw.distanceKm, raw.typicalDelayMin, origin, destination)
            origin.addRoute(route)
            route
        }
    }

    fun buildGraph(
        warehouseRaws: List<WarehouseRaw>,
        packageRaws: List<PackageRaw>,
        routeRaws: List<RouteRaw>,
        vehicleRaws: List<VehicleRaw>
    ): DomainGraph {
        val warehouseMap = buildWarehouses(warehouseRaws)

        val vehicles = buildVehicles(vehicleRaws, warehouseMap)

        val packages = buildPackages(packageRaws, warehouseMap)

        val routes = buildRoutes(routeRaws, warehouseMap)

        return DomainGraph(
            warehouses = warehouseMap.values.toList(),
            packages = packages,
            routes = routes,
            vehicles = vehicles
        )
    }
}
