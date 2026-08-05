package org.bytebloom.data.csv

import org.bytebloom.data.raw.VehicleRaw

fun parseVehicle(
    line: String,
    lineNumber: Int
): VehicleRaw? {

    val columns = line.split(",").map(String::trim)

    if (!hasExpectedColumns(columns, CsvColumns.VEHICLE, lineNumber)) {
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

    val maxCapacityKg = maxCapacityValue.toValidDouble("maximum capacity", lineNumber)
        ?: return null

    val costPerKm = costPerKmValue.toValidDouble("cost per kilometer", lineNumber)
        ?: return null

    return VehicleRaw(
        id = vehicleId,
        currentWarehouseId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
}