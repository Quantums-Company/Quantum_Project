package org.bytebloom.data.csv.parser

import org.bytebloom.data.csv.CsvColumns
import org.bytebloom.data.csv.hasExpectedColumns
import org.bytebloom.data.csv.hasRequiredValues
import org.bytebloom.data.csv.toValidDouble
import org.bytebloom.data.csv.toValidInteger
import org.bytebloom.data.raw.RouteRaw

private const val ID_INDEX = 0
private const val ORIGIN_INDEX = 1
private const val DESTINATION_INDEX = 2
private const val DISTANCE_INDEX = 3
private const val DELAY_INDEX = 4

fun parseRoute(line: String, lineNumber: Int): RouteRaw? {
    val columns = line.split(",").map(String::trim)

    return columns.takeIf { hasExpectedColumns(it, CsvColumns.ROUTE, lineNumber) }
        ?.let { extractRouteFromColumns(it, lineNumber) }
}

private fun extractRouteFromColumns(
    columns: List<String>,
    lineNumber: Int
): RouteRaw? {
    val routeId = columns[ID_INDEX].trim().uppercase()
    val originHubId = columns[ORIGIN_INDEX].trim().uppercase()
    val destinationHubId = columns[DESTINATION_INDEX].trim().uppercase()
    val distanceKm = columns[DISTANCE_INDEX].toValidDouble("distance", lineNumber)
    val typicalDelayMin = columns[DELAY_INDEX].toValidInteger("typical delay", lineNumber)

    return if (hasRequiredValues(lineNumber, "Missing required route data.", routeId, originHubId, destinationHubId)
        && distanceKm != null && typicalDelayMin != null
    ) {
        RouteRaw(
            id = routeId,
            originWarehouseId = originHubId,
            destinationWarehouseId = destinationHubId,
            distanceKm = distanceKm,
            typicalDelayMin = typicalDelayMin
        )
    } else {
        null
    }
}
