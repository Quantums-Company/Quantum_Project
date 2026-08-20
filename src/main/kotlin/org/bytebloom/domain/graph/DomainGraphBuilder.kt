package org.bytebloom.domain.graph

import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository

class DomainGraphBuilder(
    val warehouseRepository: WarehouseRepository,
    val packageRepository: PackageRepository,
    val routeRepository: RouteRepository,
    val vehicleRepository: VehicleRepository
) {
    fun buildGraph(): DomainGraph {
        val warehouses = warehouseRepository.getAll()
        val packages = packageRepository.getAll()
        val routes = routeRepository.getAll()
        val vehicles = vehicleRepository.getAll()

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