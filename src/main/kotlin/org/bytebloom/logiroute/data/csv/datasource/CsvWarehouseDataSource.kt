package org.bytebloom.logiroute.data.csv.datasource

import org.bytebloom.logiroute.data.csv.exception.CsvParsingException
import org.bytebloom.logiroute.data.csv.io.CsvFileReader
import org.bytebloom.logiroute.data.csv.io.requireExpectedColumnCount
import org.bytebloom.logiroute.data.csv.io.requireNonBlankValues
import org.bytebloom.logiroute.data.csv.io.toDoubleOrThrow
import org.bytebloom.logiroute.data.csv.raw.WarehouseRaw
import org.bytebloom.logiroute.data.csv.io.CsvTable

class CsvWarehouseDataSource(
    private val csvFileReader: CsvFileReader = CsvFileReader()
) : WarehouseDataSource {

    companion object {
        private const val ID_INDEX = 0
        private const val NAME_INDEX = 1
        private const val ZONE_INDEX = 2
        private const val LATITUDE_INDEX = 3
        private const val LONGITUDE_INDEX = 4
    }

    override suspend fun loadAll(): List<WarehouseRaw> =
        csvFileReader.loadCsv(fileName = CsvTable.WAREHOUSES.fileName, parser = ::parseWarehouse).rows

    private fun parseWarehouse(line: String, lineNumber: Int): WarehouseRaw {
        val columns = line.split(",").map(String::trim)

        requireExpectedColumnCount(columns, CsvTable.WAREHOUSES.columnCount, lineNumber)

        val id = columns[ID_INDEX].uppercase()
        val name = columns[NAME_INDEX]
        val regionalZone = columns[ZONE_INDEX]
        val latStr = columns[LATITUDE_INDEX]
        val longStr = columns[LONGITUDE_INDEX]

        requireNonBlankValues(
            lineNumber,
            "Missing required warehouse details or geographic coordinates.",
            id, name, regionalZone, latStr, longStr
        )

        val latitude = latStr.toDoubleOrThrow("latitude", lineNumber)
            ?: throw CsvParsingException("Line $lineNumber: latitude field is required and cannot be empty.")

        val longitude = longStr.toDoubleOrThrow("longitude", lineNumber)
            ?: throw CsvParsingException("Line $lineNumber: longitude field is required and cannot be empty.")

        return WarehouseRaw(
            id = id,
            name = name,
            regionalZone = regionalZone,
            latitude = latitude,
            longitude = longitude
        )
    }
}