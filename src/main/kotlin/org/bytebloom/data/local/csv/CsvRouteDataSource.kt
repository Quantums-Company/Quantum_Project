package org.bytebloom.data.local.csv

import org.bytebloom.data.exception.CsvParsingException
import org.bytebloom.data.local.common.CsvColumns
import org.bytebloom.data.local.common.CsvFileReader
import org.bytebloom.data.local.common.CsvTablesName
import org.bytebloom.data.local.common.requireExpectedColumnCount
import org.bytebloom.data.local.common.requireNonBlankValues
import org.bytebloom.data.local.common.toDoubleOrThrow
import org.bytebloom.data.local.common.toIntOrThrow
import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.data.source.csv.RouteDataSource

class CsvRouteDataSource(
    private val csvFileReader: CsvFileReader = CsvFileReader()
) : RouteDataSource {

    companion object {
        private const val ID_INDEX = 0
        private const val ORIGIN_INDEX = 1
        private const val DESTINATION_INDEX = 2
        private const val DISTANCE_INDEX = 3
        private const val DELAY_INDEX = 4
    }

    override suspend fun loadAll(): List<RouteRaw> =
        csvFileReader.loadCsv(fileName = CsvTablesName.ROUTE, parser = ::parseRoute).rows

    private fun parseRoute(line: String, lineNumber: Int): RouteRaw {
        val columns = line.split(",").map(String::trim)

        requireExpectedColumnCount(columns, CsvColumns.ROUTE, lineNumber)

        val routeId = columns[ID_INDEX].uppercase()
        val originHubId = columns[ORIGIN_INDEX].uppercase()
        val destinationHubId = columns[DESTINATION_INDEX].uppercase()
        val distanceStr = columns[DISTANCE_INDEX]
        val delayStr = columns[DELAY_INDEX]

        requireNonBlankValues(
            lineNumber,
            "Missing required route identifiers or numerical fields.",
            routeId, originHubId, destinationHubId, distanceStr, delayStr
        )

        val distanceKm = distanceStr.toDoubleOrThrow("distance", lineNumber)
            ?: throw CsvParsingException("Line $lineNumber: distance field is required and cannot be empty.")

        val typicalDelayMin = delayStr.toIntOrThrow("typical delay", lineNumber)
            ?: throw CsvParsingException("Line $lineNumber: typical delay field is required and cannot be empty.")

        return RouteRaw(
            id = routeId,
            originWarehouseId = originHubId,
            destinationWarehouseId = destinationHubId,
            distanceKm = distanceKm,
            typicalDelayMin = typicalDelayMin
        )
    }

    override suspend fun getById(): List<RouteRaw> =
        throw UnsupportedOperationException("CSV Data Source is read-only.")

    override suspend fun create(): List<RouteRaw> =
        throw UnsupportedOperationException("CSV Data Source is read-only.")

    override suspend fun update(): List<RouteRaw> =
        throw UnsupportedOperationException("CSV Data Source is read-only.")

    override suspend fun delete(): List<RouteRaw> =
        throw UnsupportedOperationException("CSV Data Source is read-only.")
}