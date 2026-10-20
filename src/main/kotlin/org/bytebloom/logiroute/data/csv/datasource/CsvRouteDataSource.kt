package org.bytebloom.logiroute.data.csv.datasource

import org.bytebloom.logiroute.data.csv.exception.CsvParsingException
import org.bytebloom.logiroute.data.csv.io.CsvFileReader
import org.bytebloom.logiroute.data.csv.io.requireExpectedColumnCount
import org.bytebloom.logiroute.data.csv.io.requireNonBlankValues
import org.bytebloom.logiroute.data.csv.io.toDoubleOrThrow
import org.bytebloom.logiroute.data.csv.io.toIntOrThrow
import org.bytebloom.logiroute.data.csv.raw.RouteRaw
import org.bytebloom.logiroute.data.csv.io.CsvTable

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
        csvFileReader.loadCsv(fileName = CsvTable.ROUTES.fileName, parser = ::parseRoute).rows

    private fun parseRoute(line: String, lineNumber: Int): RouteRaw {
        val columns = line.split(",").map(String::trim)

        requireExpectedColumnCount(columns, CsvTable.ROUTES.columnCount, lineNumber)

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
}