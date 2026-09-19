package org.bytebloom.presentation

import kotlinx.coroutines.runBlocking
import org.bytebloom.data.repository.remote.RemotePackageRepository
import org.bytebloom.data.repository.remote.RemoteRouteRepository
import org.bytebloom.data.repository.remote.RemoteVehicleRepository
import org.bytebloom.data.repository.remote.RemoteWarehouseRepository
import org.bytebloom.data.remote.client.SupabaseClientProvider
import org.bytebloom.presentation.DemoRunner
import org.bytebloom.data.remote.SdkPackageDataSource
import org.bytebloom.data.remote.SdkWarehouseDataSource
import org.bytebloom.data.remote.SdkRouteDataSource
import org.bytebloom.data.remote.SdkVehicleDataSource

fun main() = runBlocking {
//    val warehouseRepo: WarehouseRepository =
//        CsvWarehouseRepository(CsvWarehouseDataSource())
//
//    val warehousesById =
//        warehouseRepo
//            .getAll()
//            .associateBy { it.id }
//
//    val packageRepo: PackageRepository =
//        CsvPackageRepository(warehousesById, CsvPackageDataSource())
//
//    val routeRepo: RouteRepository =
//        CsvRouteRepository(warehousesById, CsvRouteDataSource())
//
//    val vehicleRepo: VehicleRepository =
//        CsvVehicleRepository(warehousesById, CsvVehicleDataSource())
//
//    DemoRunner(
//        warehouseRepository = warehouseRepo,
//        packageRepository = packageRepo,
//        routeRepository = routeRepo,
//        vehicleRepository = vehicleRepo
//    ).run()

    val client = SupabaseClientProvider.create()

    val warehouseRepo = RemoteWarehouseRepository(SdkWarehouseDataSource(client))
        val warehousesById =
        warehouseRepo
            .getAll()
            .associateBy { it.id }

    val vehicleRepo = RemoteVehicleRepository(warehousesById, SdkVehicleDataSource(client))
    val routeRepo = RemoteRouteRepository(warehousesById, SdkRouteDataSource(client))
    val packageRepo = RemotePackageRepository(warehousesById, SdkPackageDataSource(client))

    val warehouses = warehouseRepo.getAll()
    val vehicles = vehicleRepo.getAll()
    val routes = routeRepo.getAll()

    println("--- Warehouses ---")
    try {
        warehouseRepo.getAll().forEach { println(it) }
    } catch (e: Throwable) {
        println("Error fetching warehouses: ${e.message}")
    }

    println("--- Vehicles ---")
    try {
        vehicleRepo.getAll().forEach {
            println("Vehicle(id=${it.id}, capacity=${it.maxCapacityKg}, warehouse=${it.currentWarehouse.id})")
        }
    } catch (e: Throwable) {
        println("Error fetching vehicles: ${e.message}")
    }

    println("--- Routes ---")
    try {
        routeRepo.getAll().forEach {
            println("Route(id=${it.id}, ${it.originWarehouse.id} -> ${it.destinationWarehouse.id}, ${it.distanceKm}km)")
        }
    } catch (e: Throwable) {
        println("Error fetching routes: ${e.message}")
    }

    println("--- Packages ---")
    try {
        packageRepo.getAll().forEach {
            println("Package(id=${it.id}, weight=${it.weight}, priority=${it.priority})")
        }
    } catch (e: Throwable) {
        println("Error fetching packages: ${e.message}")
    }

    demonstrateGreedyDispatcher(
        warehouses = warehouses,
        vehicles = vehicles,
        routes = routes
    )

}


