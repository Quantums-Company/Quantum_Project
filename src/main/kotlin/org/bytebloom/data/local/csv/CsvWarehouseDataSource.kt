package org.bytebloom.data.local.csv

import org.bytebloom.data.local.csv.common.CsvColumns
import org.bytebloom.data.local.csv.common.CsvTablesName
import org.bytebloom.data.local.csv.common.hasExpectedColumns
import org.bytebloom.data.local.csv.common.hasRequiredValues
import org.bytebloom.data.local.csv.common.loadCsv
import org.bytebloom.data.local.csv.common.toValidDouble
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.data.source.WarehouseDataSource

class CsvWarehouseDataSource: WarehouseDataSource {
    companion object {
        private const val ID_INDEX = 0
        private const val NAME_INDEX = 1
        private const val ZONE_INDEX = 2
        private const val LATITUDE_INDEX = 3
        private const val LONGITUDE_INDEX = 4
    }

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

    override fun loadAll(): List<WarehouseRaw> =
        loadCsv(fileName = CsvTablesName.WAREHOUSE, parser = ::parseWarehouse)
}