package org.bytebloom.data.csv.parser

import org.bytebloom.data.csv.CsvColumns
import org.bytebloom.data.csv.hasExpectedColumns
import org.bytebloom.data.csv.hasRequiredValues
import org.bytebloom.data.csv.toValidDouble
import org.bytebloom.data.raw.WarehouseRaw

private const val ID_INDEX = 0
private const val NAME_INDEX = 1
private const val ZONE_INDEX = 2
private const val LATITUDE_INDEX = 3
private const val LONGITUDE_INDEX = 4

fun parseWarehouse(line: String, lineNumber: Int): WarehouseRaw? {
    val columns = line.split(",").map(String::trim)

    return columns.takeIf { hasExpectedColumns(it, CsvColumns.WAREHOUSE, lineNumber) }
        ?.let { extractWarehouseFromColumns(it, lineNumber) }
}

private fun extractWarehouseFromColumns(
    columns: List<String>,
    lineNumber: Int
): WarehouseRaw? {
    val id = columns[ID_INDEX].trim().uppercase()
    val name = columns[NAME_INDEX]
    val regionalZone = columns[ZONE_INDEX]
    val latitude = columns[LATITUDE_INDEX].toValidDouble("latitude", lineNumber)
    val longitude = columns[LONGITUDE_INDEX].toValidDouble("longitude", lineNumber)

    return if (hasRequiredValues(lineNumber, "Missing required warehouse data.", id, name, regionalZone)
        && latitude != null && longitude != null
    ) {
        WarehouseRaw(
            id = id,
            name = name,
            regionalZone = regionalZone,
            latitude = latitude,
            longitude = longitude
        )
    } else {
        null
    }
}
