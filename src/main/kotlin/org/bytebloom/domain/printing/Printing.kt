package org.bytebloom.domain.printing

import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.domain.hashing.ValidationReport
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
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
                    if (vehicle.nonMigrationPassed) "PASSED"
                    else "FAILED"
                }"
            )
        }

        Logger.info("----------------------------------------------------------")
    }

    Logger.info("")

    if (report.allChecksPassed) {

        Logger.info("SUCCESS")
        Logger.info("Consistent Hashing validation PASSED.")

    } else {

        Logger.info("FAILURE")
        Logger.info("Cargo migration detected.")
    }

    Logger.info("==================================================================")
}

fun calculatePathDistance(graph: WarehouseGraph, path: List<String>): Double {
    if (path.size < 2) return 0.0

    var totalDistance = 0.0
    val map = graph.adjacencyMap

    for (i in 0 until path.size - 1) {
        val current = path[i]
        val next = path[i + 1]

        // Get the distance from the neighbor map of the current warehouse
        val distance = map[current]?.get(next) ?: 0.0
        totalDistance += distance
    }

    return totalDistance
}


fun printRouteComparison(graph: WarehouseGraph, start: String, destination: String, bfsPath: List<String>, dijkstraPath: List<String>) {
    val bfsDistance = calculatePathDistance(graph, bfsPath)
    val dijkstraDistance = calculatePathDistance(graph, dijkstraPath)

    println("=== Route Algorithm Comparison ===")
    println("Start: $start | Destination: $destination")
    println()

    println("--- BFS Result (Fewest Hops) ---")
    println("Path: ${bfsPath.joinToString(" -> ")}")
    println("Total Hops: ${bfsPath.size - 1}")
    println("Total Distance: %.2f km".format(bfsDistance))
    println()

    println("--- Dijkstra Result (Shortest Distance) ---")
    println("Path: ${dijkstraPath.joinToString(" -> ")}")
    println("Total Hops: ${dijkstraPath.size - 1}")
    println("Total Distance: %.2f km".format(dijkstraDistance))
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