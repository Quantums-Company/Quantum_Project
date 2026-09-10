package org.bytebloom.data.local.csv

import org.bytebloom.data.local.csv.common.CsvColumns
import org.bytebloom.data.local.csv.common.CsvTablesName
import org.bytebloom.data.local.csv.common.hasExpectedColumns
import org.bytebloom.data.local.csv.common.hasRequiredValues
import org.bytebloom.data.local.csv.common.loadCsv
import org.bytebloom.data.local.csv.common.toValidDouble
import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.data.source.PackageDataSource
import org.bytebloom.domain.model.Priority

class CsvPackageDataSource: PackageDataSource {
    companion object {
        private const val ID_INDEX = 0
        private const val WEIGHT_INDEX = 1
        private const val ORIGIN_INDEX = 2
        private const val DESTINATION_INDEX = 3
        private const val PRIORITY_INDEX = 4
    }

    private fun parsePackage(line: String, lineNumber: Int): PackageRaw? {
        val columns = line.split(",").map(String::trim)

        return columns.takeIf { hasExpectedColumns(it, CsvColumns.PACKAGE, lineNumber) }
            ?.let { extractPackageFromColumns(it, lineNumber) }
    }

    private fun extractPackageFromColumns(
        columns: List<String>,
        lineNumber: Int
    ): PackageRaw? {
        val id = columns[ID_INDEX].trim().uppercase()
        val weightValue = columns[WEIGHT_INDEX]
        val originHubId = columns[ORIGIN_INDEX].trim().uppercase()
        val destinationHubId = columns[DESTINATION_INDEX].trim().uppercase()
        val priorityValue = columns[PRIORITY_INDEX]

        val weight = weightValue.toValidDouble("weight", lineNumber)

        val hasValues = hasRequiredValues(
            lineNumber, "Missing required data.", id, destinationHubId, originHubId
        )

        return if (hasValues && weight != null) {
            PackageRaw(
                id = id,
                weight = weight,
                originWarehouseId = originHubId,
                destinationWarehouseId = destinationHubId,
                priority = Priority.from(priorityValue)
            )
        } else {
            null
        }
    }
    override fun loadAll(): List<PackageRaw> =
        loadCsv(fileName = CsvTablesName.PACKAGE, parser = ::parsePackage)
}