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
