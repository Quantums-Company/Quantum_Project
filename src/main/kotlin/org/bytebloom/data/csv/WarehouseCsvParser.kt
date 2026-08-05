package org.bytebloom.data.csv

import org.bytebloom.data.raw.WarehouseRaw

fun parseWarehouse(
    line: String,
    lineNumber: Int
): WarehouseRaw? {

    val columns = line.split(",").map(String::trim)

    if (!hasExpectedColumns(columns, CsvColumns.WAREHOUSE, lineNumber)) {
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

    val latitude = latitudeValue.toValidDouble("latitude", lineNumber)?: return null
    val longitude = longitudeValue.toValidDouble("longitude", lineNumber)?: return null

    return WarehouseRaw(
        id = id,
        name = name,
        regionalZone = regionalZone,
        latitude = latitude,
        longitude = longitude
    )
}