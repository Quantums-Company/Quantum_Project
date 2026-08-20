package org.bytebloom.domain.printing

import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.domain.vehicleReshuffling.ValidationReport
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.util.Logger

private const val SEPARATOR_WIDTH = 120

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
    val separator = "=".repeat(SEPARATOR_WIDTH)
    Logger.info("\n$separator")
    Logger.info("=== Cargo Queue for ${firstWarehouse.name} ===")
    Logger.info("$separator\n")

    firstWarehouse.cargoQueue.forEach { Logger.info(it.toString()) }
    Logger.info("\n$separator\n")
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
            printBrokenVehicle(vehicle)
        } else {
            printHealthyVehicle(vehicle)
        }
        Logger.info("----------------------------------------------------------")
    }

    Logger.info("")
    printValidationResult(report.allChecksPassed)
    Logger.info("==================================================================")
}

private fun printBrokenVehicle(vehicle: org.bytebloom.domain.vehicleReshuffling.VehicleValidation) {
    Logger.info("[X] Vehicle Slot ${vehicle.slot}")
    Logger.info("    Status : BROKEN")
    Logger.info("    Packages Before : ${vehicle.packageCountBefore}")
}

private fun printHealthyVehicle(vehicle: org.bytebloom.domain.vehicleReshuffling.VehicleValidation) {
    Logger.info("[✓] Vehicle Slot ${vehicle.slot}")
    Logger.info("    Packages Before : ${vehicle.packageCountBefore}")
    Logger.info("    Packages After  : ${vehicle.packageCountAfter}")
    val status = if (vehicle.nonMigrationPassed) "PASSED" else "FAILED"
    Logger.info("    Non-Migration   : $status")
}

private fun printValidationResult(allChecksPassed: Boolean) {
    if (allChecksPassed) {
        Logger.info("SUCCESS")
        Logger.info("Consistent Hashing validation PASSED.")
    } else {
        Logger.info("FAILURE")
        Logger.info("Cargo migration detected.")
    }
}

fun calculatePathDistance(graph: WarehouseGraph, path: List<Warehouse>): Double {
    if (path.size < 2) return 0.0

    var totalDistance = 0.0
    val map = graph.adjacencyMap

    for (i in 0 until path.size - 1) {
        val current = path[i]
        val next = path[i + 1]
        val distance = map[current]?.get(next) ?: 0.0
        totalDistance += distance
    }

    return totalDistance
}

fun printRouteComparison(
    graph: WarehouseGraph,
    start: Warehouse,
    destination: Warehouse,
    unidirectionalBfsPath: List<Warehouse>,
    bidirectionalBfsPath: List<Warehouse>,
    dijkstraPath: List<Warehouse>
) {
    val unidirectionalBfsDistance = calculatePathDistance(graph, unidirectionalBfsPath)
    val bidirectionalBfsDistance = calculatePathDistance(graph, bidirectionalBfsPath)
    val dijkstraDistance = calculatePathDistance(graph, dijkstraPath)

    Logger.info("\n\n=== Route Algorithm Comparison ===")
    Logger.info("Start: ${start.id} | Destination: ${destination.id}")
    Logger.info("")

    Logger.info("--- Unidirectional BFS Result (Fewest Hops) ---")
    Logger.info("Path: ${unidirectionalBfsPath.joinToString(" -> "){it.id}}")
    Logger.info("Total Hops: ${unidirectionalBfsPath.size - 1}")
    Logger.info("Total Distance: %.2f km".format(unidirectionalBfsDistance))
    Logger.info("")

    Logger.info("--- Bidirectional BFS Result (Fewest Hops) ---")
    Logger.info("Path: ${bidirectionalBfsPath.joinToString(" -> "){it.id}}")
    Logger.info("Total Hops: ${bidirectionalBfsPath.size - 1}")
    Logger.info("Total Distance: %.2f km".format(bidirectionalBfsDistance))
    Logger.info("")

    Logger.info("--- Dijkstra Result (Shortest Distance) ---")
    Logger.info("Path: ${dijkstraPath.joinToString(" -> "){it.id}}")
    Logger.info("Total Hops: ${dijkstraPath.size - 1}")
    Logger.info("Total Distance: %.2f km".format(dijkstraDistance))
}
