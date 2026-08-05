package org.bytebloom.domain.printing

import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.domain.hashing.ValidationReport
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.util.Logger

fun printTopPackagesRaw(packages: List<PackageRaw>, count: Int) {
    Logger.info("\nTop Priority Packages:\n")

    packages.take(count).forEachIndexed { index, packageItem ->
        Logger.info("${index + 1}.")
        Logger.info("ID: ${packageItem.id}")
        Logger.info("Weight: ${packageItem.weight}")
        Logger.info("Origin: ${packageItem.originWarehouseId}")
        Logger.info("Priority: ${packageItem.priority}")
        Logger.info("Destination: ${packageItem.destinationWarehouseId}")
        Logger.info("")
    }
}

fun printPackagesForFirstWarehouse(firstWarehouse: Warehouse) {
    Logger.info("\n======================================================================================================================")
    Logger.info("\"=== Cargo Queue for $firstWarehouse.name ===\"")
    Logger.info("======================================================================================================================\n")

    firstWarehouse.cargoQueue.forEach(::println)
    Logger.info("\n======================================================================================================================\n")
}

fun printResilienceReport(report: ValidationReport) {

    Logger.info("==================================================================")
    Logger.info("          PACKAGE ASSIGNMENT RING: RESILIENCE AUDIT REPORT")
    Logger.info("==================================================================")

    Logger.info("Broken Vehicle Slot : ${report.brokenSlot}")
    Logger.info("Affected Packages   : ${report.reroutedPackages}")

    Logger.info("")

    report.vehicles.forEach { vehicle ->

        if (vehicle.isBroken) {

            Logger.info("[X] Vehicle Slot ${vehicle.slot}")
            Logger.info("    Status : BROKEN")
            Logger.info("    Packages Before : ${vehicle.packageCountBefore}")

        } else {

            Logger.info("[✓] Vehicle Slot ${vehicle.slot}")
            Logger.info("    Packages Before : ${vehicle.packageCountBefore}")
            Logger.info("    Packages After  : ${vehicle.packageCountAfter}")
            Logger.info(
                "    Non-Migration   : ${
                    if (vehicle.isPassed) "PASSED"
                    else "FAILED"
                }"
            )
        }

        Logger.info("----------------------------------------------------------")
    }

    Logger.info("")

    if (report.passed) {

        Logger.info("SUCCESS")
        Logger.info("Consistent Hashing validation PASSED.")

    } else {

        Logger.info("FAILURE")
        Logger.info("Cargo migration detected.")
    }

    Logger.info("==================================================================")
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