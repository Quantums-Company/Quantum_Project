package org.bytebloom.domain.model.logic

import org.bytebloom.domain.model.dataHolder.packageRaw
import org.bytebloom.domain.model.Warehouse

fun printTopPackagesRaw(packages: List<packageRaw>, count: Int) {
    println("\nTop Priority Packages:\n")

    packages.take(count).forEachIndexed { index, packageItem ->
        println("${index + 1}.")
        println("ID: ${packageItem.id}")
        println("Weight: ${packageItem.weight}")
        println("Origin: ${packageItem.originWarehouseId}")
        println("Priority: ${packageItem.priority}")
        println("Destination: ${packageItem.destinationWarehouseId}")
        println()
    }
}

fun printPackagesForFirstWarehouse(firstWarehouse: Warehouse) {
    println("\n======================================================================================================================")
    println("\"=== Cargo Queue for $firstWarehouse.name ===\"")
    println("======================================================================================================================\n")

    firstWarehouse.cargoQueue.forEach(::println)
    println("\n======================================================================================================================\n")
}
//
//fun printPackages(packages: List<Package>) {
//    packages.forEach(::println)
//}
//
//fun printWarehouses(Warehouses: List<Warehouse>) {
//    Warehouses.forEach(::println)
//}
//
//fun printVehicles(Vehicles: List<Vehicle>) {
//    Vehicles.forEach(::println)
//}
//
//fun printRoutes(Routes: List<Route>) {
//    Routes.forEach(::println)
//}