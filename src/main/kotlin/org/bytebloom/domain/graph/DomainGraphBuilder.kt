package org.bytebloom.domain.graph

import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository

object DomainGraphBuilder {
    fun buildGraph(
        warehouseRepository: WarehouseRepository,
        packageRepository: PackageRepository,
        routeRepository: RouteRepository,
        vehicleRepository: VehicleRepository
    ): DomainGraph {
        val warehouses = warehouseRepository.getAllWarehouses()
        val packages = packageRepository.getAllPackages()
        val routes = routeRepository.getAllRoutes()
        val vehicles = vehicleRepository.getAllVehicles()

        packages.forEach { packageItem ->
            packageItem.originWarehouse.addPackage(packageItem)
        }

        routes.forEach { route ->
            route.originWarehouse.addRoute(route)
        }

        vehicles.forEach { vehicle ->
            vehicle.currentWarehouse.addVehicle(vehicle)
        }


        return DomainGraph(
            warehouses = warehouses,
            packages = packages,
            routes = routes,
            vehicles = vehicles
        )
    }
}