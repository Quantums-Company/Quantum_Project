package org.bytebloom.data.csv

import org.bytebloom.data.raw.RouteRaw

fun parseRoute(
    line: String,
    lineNumber: Int
): RouteRaw? {

    val columns = line.split(",").map(String::trim)

    if (!hasExpectedColumns(columns, CsvColumns.ROUTE, lineNumber)) {
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
        distanceValue.toValidDouble("distance", lineNumber)
            ?: return null

    val typicalDelayMin =
        typicalDelayValue.toValidInteger( "typical delay", lineNumber)
            ?: return null

    return RouteRaw(
        id = routeId,
        originWarehouseId = originHubId,
        destinationWarehouseId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
}