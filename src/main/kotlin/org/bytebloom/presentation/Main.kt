package org.bytebloom.presentation

import kotlinx.coroutines.runBlocking
import org.bytebloom.data.repository.CsvPackageRepository
import org.bytebloom.data.repository.CsvRouteRepository
import org.bytebloom.data.repository.CsvVehicleRepository
import org.bytebloom.data.repository.CsvWarehouseRepository
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.data.local.csv.CsvPackageDataSource
import org.bytebloom.data.local.csv.CsvRouteDataSource
import org.bytebloom.data.local.csv.CsvVehicleDataSource
import org.bytebloom.data.local.csv.CsvWarehouseDataSource
import org.bytebloom.data.local.supabase.SupabasePackageRepository
import org.bytebloom.data.local.supabase.SupabaseRouteRepository
import org.bytebloom.data.local.supabase.SupabaseVehicleRepository
import org.bytebloom.data.local.supabase.SupabaseWarehouseRepository
import org.bytebloom.data.remote.client.SupabaseClientProvider

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

    val warehouseRepo = SupabaseWarehouseRepository(client)
    val vehicleRepo = SupabaseVehicleRepository(client, warehouseRepo)
    val routeRepo = SupabaseRouteRepository(client, warehouseRepo)
    val packageRepo = SupabasePackageRepository(client, warehouseRepo)

    println("--- Warehouses ---")
    try {
        warehouseRepo.getAll().forEach { println(it) }
    } catch (e: Exception) {
        println("Error fetching warehouses: ${e.message}")
    }

    println("--- Vehicles ---")
    try {
        vehicleRepo.getAll().forEach {
            println("Vehicle(id=${it.id}, capacity=${it.maxCapacityKg}, warehouse=${it.currentWarehouse.id})")
        }
    } catch (e: Exception) {
        println("Error fetching vehicles: ${e.message}")
    }

    println("--- Routes ---")
    try {
        routeRepo.getAll().forEach {
            println("Route(id=${it.id}, ${it.originWarehouse.id} -> ${it.destinationWarehouse.id}, ${it.distanceKm}km)")
        }
    } catch (e: Exception) {
        println("Error fetching routes: ${e.message}")
    }

    println("--- Packages ---")
    try {
        packageRepo.getAll().forEach {
            println("Package(id=${it.id}, weight=${it.weight}, priority=${it.priority})")
        }
    } catch (e: Exception) {
        println("Error fetching packages: ${e.message}")
    }
}


