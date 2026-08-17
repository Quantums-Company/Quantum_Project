package org.bytebloom.data.csv.parser

import org.bytebloom.data.csv.CsvColumns
import org.bytebloom.data.csv.hasExpectedColumns
import org.bytebloom.data.csv.hasRequiredValues
import org.bytebloom.data.csv.toValidDouble
import org.bytebloom.data.raw.VehicleRaw

private const val ID_INDEX = 0
private const val HUB_INDEX = 1
private const val CAPACITY_INDEX = 2
private const val COST_INDEX = 3

fun parseVehicle(line: String, lineNumber: Int): VehicleRaw? {
    val columns = line.split(",").map(String::trim)

    return columns.takeIf { hasExpectedColumns(it, CsvColumns.VEHICLE, lineNumber) }
        ?.let { extractVehicleFromColumns(it, lineNumber) }
}

private fun extractVehicleFromColumns(
    columns: List<String>,
    lineNumber: Int
): VehicleRaw? {
    val vehicleId = columns[ID_INDEX].trim().uppercase()
    val currentHubId = columns[HUB_INDEX].trim().uppercase()
    val maxCapacityKg = columns[CAPACITY_INDEX].toValidDouble("maximum capacity", lineNumber)
    val costPerKm = columns[COST_INDEX].toValidDouble("cost per kilometer", lineNumber)

    return if (hasRequiredValues(lineNumber, "Missing required vehicle data.", vehicleId, currentHubId)
        && maxCapacityKg != null && costPerKm != null
    ) {
        VehicleRaw(
            id = vehicleId,
            currentWarehouseId = currentHubId,
            maxCapacityKg = maxCapacityKg,
            costPerKm = costPerKm
        )
    } else {
        null
    }
}
