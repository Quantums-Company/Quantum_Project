package org.bytebloom.data.csv.perser

import org.bytebloom.data.csv.CsvColumns
import org.bytebloom.data.csv.hasExpectedColumns
import org.bytebloom.data.csv.hasRequiredValues
import org.bytebloom.data.csv.toValidDouble
import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.data.raw.Priority

fun parsePackage(line: String, lineNumber: Int): PackageRaw? {
    val columns = line.split(",").map(String::trim)

    if (!hasExpectedColumns(columns, CsvColumns.PACKAGE, lineNumber)) {
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

    val weight = weightValue.toValidDouble("weight",lineNumber)?: return null

    return PackageRaw(
        id = id,
        weight = weight,
        originWarehouseId = originHubId,
        destinationWarehouseId = destinationHubId,
        priority = Priority.from(priorityValue)
    )
}