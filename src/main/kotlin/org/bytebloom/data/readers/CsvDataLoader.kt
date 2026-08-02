package org.bytebloom.data.readers

import org.bytebloom.data.dataHolder.Priority
import org.bytebloom.data.dataHolder.packageRaw
import org.bytebloom.data.dataHolder.routeRaw
import org.bytebloom.data.dataHolder.vehicleRaw
import org.bytebloom.data.dataHolder.warehouseRaw
import java.io.File
import java.io.IOException

private const val RESOURCE_PATH = "src/resources"
private const val PACKAGE_COLUMNS = 5
private const val VEHICLE_COLUMNS = 4
private const val ROUTE_COLUMNS = 5
private const val WAREHOUSE_COLUMNS = 5

private fun hasExpectedColumns(
    columns: List<String>,
    expected: Int,
    lineNumber: Int
): Boolean {
    if (columns.size != expected) {
        println(
            "Warning: Skipping line $lineNumber. " +
                    "Expected $expected columns but found ${columns.size}."
        )
        return false
    }

    return true
}

private fun hasRequiredValues(
    lineNumber: Int,
    message: String,
    vararg values: String
): Boolean {
    if (values.any(String::isBlank)) {
        println(
            "Warning: Skipping line $lineNumber. " +
                    message
        )
        return false
    }

    return true
}

private fun parseDouble(
    value: String,
    fieldName: String,
    lineNumber: Int
): Double? {
    if (value.equals("null", ignoreCase = true) || value.isBlank()) {
        return null
    }

    return value.toDoubleOrNull() ?: run {
        println(
            "Warning: Skipping line $lineNumber. " +
                    "Invalid $fieldName '$value'."
        )
        null
    }
}

private fun parseInt(
    value: String,
    fieldName: String,
    lineNumber: Int
): Int? {
    return value.toIntOrNull() ?: run {
        println(
            "Warning: Skipping line $lineNumber. " +
                    "Invalid $fieldName '$value'."
        )
        null
    }
}

fun parsePriority(value: String): Priority {
    return when (value.trim().uppercase()) {
        "URGENT" -> Priority.URGENT
        "STANDARD" -> Priority.STANDARD
        "LOW" -> Priority.LOW
        else -> {
            println("Warning: Unknown priority '$value'. Using LOW as default.")
            Priority.LOW
        }
    }
}

private fun parsePackage(line: String, lineNumber: Int): packageRaw? {
    val columns = line.split(",").map(String::trim)

    if (!hasExpectedColumns(columns, PACKAGE_COLUMNS, lineNumber)) {
        return null
    }

    val id = columns[0].trim().uppercase()
    val weightValue = columns[1]
    val originHubId = columns[2].trim().uppercase()
    val destinationHubId = columns[3].trim().uppercase()
    val priorityValue = columns[4]

    if (
        !hasRequiredValues(
            lineNumber,
            "Missing required data (ID or Destination).",
            id,
            destinationHubId,
            originHubId
        )
    ) {
        return null
    }

    val weight =parseDouble(weightValue, "weight", lineNumber)?: return null

    return packageRaw(
        id = id,
        weight = weight,
        originWarehouseId = originHubId,
        destinationWarehouseId = destinationHubId,
        priority = parsePriority(priorityValue)
    )
}

fun readPackages(fileName: String): List<packageRaw> {
    val packages = mutableListOf<packageRaw>()

    readCsvFile(fileName) { line, lineNumber ->
        parsePackage(line, lineNumber)?.let(packages::add)
    }

    println("Successfully parsed packages: ${packages.size}")
    return packages
}

private fun parseVehicle(
    line: String,
    lineNumber: Int
): vehicleRaw? {

    val columns = line.split(",").map(String::trim)

    if (!hasExpectedColumns(columns, VEHICLE_COLUMNS, lineNumber)) {
        return null
    }

    val vehicleId = columns[0].trim().uppercase()
    val currentHubId = columns[1].trim().uppercase()
    val maxCapacityValue = columns[2]
    val costPerKmValue = columns[3]

    if (
        !hasRequiredValues(
            lineNumber,
            "Missing required data (Vehicle ID or Current Hub ID).",
            vehicleId,
            currentHubId
        )
    ) {
        return null
    }

    val maxCapacityKg = parseDouble(maxCapacityValue, "maximum capacity", lineNumber)
            ?: return null

    val costPerKm = parseDouble(costPerKmValue, "cost per kilometer", lineNumber)
            ?: return null

    return vehicleRaw(
        id = vehicleId,
        currentHubId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
}

fun readVehicles(fileName: String): List<vehicleRaw> {
    val vehicles = mutableListOf<vehicleRaw>()

    readCsvFile(fileName) { line, lineNumber ->
        parseVehicle(line, lineNumber)?.let(vehicles::add)
    }

    println("Successfully parsed vehicles: ${vehicles.size}")
    return vehicles
}

private fun parseRoute(
    line: String,
    lineNumber: Int
): routeRaw? {

    val columns = line.split(",").map(String::trim)

    if (!hasExpectedColumns(columns, ROUTE_COLUMNS, lineNumber)) {
        return null
    }

    val routeId = columns[0].trim().uppercase()
    val originHubId = columns[1].trim().uppercase()
    val destinationHubId = columns[2].trim().uppercase()
    val distanceValue = columns[3]
    val typicalDelayValue = columns[4]

    if (
        !hasRequiredValues(
            lineNumber,
            "Missing required data (Route ID, Origin Hub ID, or Destination Hub ID).",
            routeId,
            originHubId,
            destinationHubId
        )
    ) {
        return null
    }

    val distanceKm =
        parseDouble(distanceValue, "distance", lineNumber)
            ?: return null

    val typicalDelayMin =
        parseInt(typicalDelayValue, "typical delay", lineNumber)
            ?: return null

    return routeRaw(
        id = routeId,
        originWarehouseI = originHubId,
        destinationWarehouseId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
}

fun readRoutes(fileName: String): List<routeRaw> {
    val routes = mutableListOf<routeRaw>()

    readCsvFile(fileName) { line, lineNumber ->
        parseRoute(line, lineNumber)?.let(routes::add)
    }

    println("Successfully parsed routes: ${routes.size}")
    return routes
}

private fun parseWarehouse(
    line: String,
    lineNumber: Int
): warehouseRaw? {

    val columns = line.split(",").map(String::trim)

    if (!hasExpectedColumns(columns, WAREHOUSE_COLUMNS, lineNumber)) {
        return null
    }

    val id = columns[0].trim().uppercase()
    val name = columns[1]
    val regionalZone = columns[2]
    val latitudeValue = columns[3]
    val longitudeValue = columns[4]

    if (
        !hasRequiredValues(
            lineNumber,
            "Missing required warehouse data.",
            id,
            name,
            regionalZone
        )
    ) {
        return null
    }

    val latitude = parseDouble(latitudeValue, "latitude", lineNumber)
    val longitude = parseDouble(longitudeValue, "longitude", lineNumber)

    if (longitude == null || latitude == null) {
        println("Warning: Skipping warehouse at line $lineNumber.")
        return null
    }

    return warehouseRaw(
        id = id,
        name = name,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}

fun readWarehouses(fileName: String): List<warehouseRaw> {
    val warehouses = mutableListOf<warehouseRaw>()

    readCsvFile(fileName) { line, lineNumber ->
        parseWarehouse(line, lineNumber)?.let(warehouses::add)
    }

    println("Successfully parsed routes: ${warehouses.size}")
    return warehouses
}

private fun readCsvFile(
    fileName: String,
    processLine: (String, Int) -> Unit
) {
    val file = File(RESOURCE_PATH, fileName)

    if (!file.exists()) {
        println("Warning: File '$fileName' was not found!")
        return
    }

    try {
        file.useLines { lines ->
            lines.drop(1).forEachIndexed { index, line ->
                if (line.isNotBlank()) {
                    processLine(line, index + 2)
                }
            }
        }
    } catch (e: IOException) {
        println("Error reading file '$fileName': ${e.message}")
    }
}