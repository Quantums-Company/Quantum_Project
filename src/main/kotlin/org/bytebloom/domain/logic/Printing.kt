package org.bytebloom.domain.logic

import org.bytebloom.data.dataHolder.PackageRaw
import org.bytebloom.domain.model.Warehouse

fun printTopPackagesRaw(packages: List<PackageRaw>, count: Int) {
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

fun printResilienceReport(report: ValidationReport) {

    println("==================================================================")
    println("          PACKAGE ASSIGNMENT RING: RESILIENCE AUDIT REPORT")
    println("==================================================================")

    println("Broken Vehicle Slot : ${report.brokenSlot}")
    println("Affected Packages   : ${report.reroutedPackages}")

    println()

    report.vehicles.forEach { vehicle ->

        if (vehicle.isBroken) {

            println("[X] Vehicle Slot ${vehicle.slot}")
            println("    Status : BROKEN")
            println("    Packages Before : ${vehicle.packageCountBefore}")

        } else {

            println("[✓] Vehicle Slot ${vehicle.slot}")
            println("    Packages Before : ${vehicle.packageCountBefore}")
            println("    Packages After  : ${vehicle.packageCountAfter}")
            println(
                "    Non-Migration   : ${
                    if (vehicle.isPassed) "PASSED"
                    else "FAILED"
                }"
            )
        }

        println("----------------------------------------------------------")
    }

    println()

    if (report.passed) {

        println("SUCCESS")
        println("Consistent Hashing validation PASSED.")

    } else {

        println("FAILURE")
        println("Cargo migration detected.")
    }

    println("==================================================================")
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