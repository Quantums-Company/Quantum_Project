package org.bytebloom.logic

import org.bytebloom.dataHolder.packageRaw
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle

fun printTopPackagesRaw(packages: List<packageRaw>, count: Int) {
    println("\nTop Priority Packages:\n")

    packages.take(count).forEachIndexed { index, packageItem ->
        println("${index + 1}.")
        println("ID: ${packageItem.id}")
        println("Weight: ${packageItem.weight}")
        println("Origin: ${packageItem.originHubId}")
        println("Priority: ${packageItem.priority}")
        println("Destination: ${packageItem.destinationHubId}")
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