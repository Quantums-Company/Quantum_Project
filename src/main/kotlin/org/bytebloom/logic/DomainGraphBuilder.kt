package org.bytebloom.logic

import org.bytebloom.dataHolder.packageRaw
import org.bytebloom.dataHolder.routeRaw
import org.bytebloom.dataHolder.vehicleRaw
import org.bytebloom.dataHolder.warehouseRaw
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
        warehouseRaws: List<warehouseRaw>
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
        vehicleRaws: List<vehicleRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Vehicle> {

        val vehiclesByHub = vehicleRaws.groupBy { it.currentHubId }

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
        packageRaws: List<packageRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Package> {
        return packageRaws.mapNotNull { raw ->
            val origin = findWarehouse(warehouseMap, raw.originHubId, "Package")
            val destination = findWarehouse(warehouseMap, raw.destinationHubId, "Package")

            if (origin == null || destination == null) {
                return@mapNotNull null
            }
            val pkg = Package(raw.id, raw.weight, raw.priority, origin, destination)

            origin.addPackage(pkg)
            pkg
        }
    }

    private fun buildRoutes(
        routeRaws: List<routeRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Route> {
        return routeRaws.mapNotNull { raw ->
            val origin = findWarehouse(warehouseMap, raw.originHubId, "Route")
            val destination = findWarehouse(warehouseMap, raw.destinationHubId, "Route")

            if (origin == null || destination == null) {
                return@mapNotNull null
            }
            val route = Route(raw.id, raw.distanceKm, raw.typicalDelayMin, origin, destination)
            origin.addRoute(route)
            route
        }
    }

    fun buildGraph(
        warehouseRaws: List<warehouseRaw>,
        packageRaws: List<packageRaw>,
        routeRaws: List<routeRaw>,
        vehicleRaws: List<vehicleRaw>
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
