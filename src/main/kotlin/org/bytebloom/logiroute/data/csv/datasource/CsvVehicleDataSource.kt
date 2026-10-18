package org.bytebloom.logiroute.data.csv.datasource

import org.bytebloom.logiroute.data.csv.exception.CsvParsingException
import org.bytebloom.logiroute.data.csv.io.CsvColumns
import org.bytebloom.logiroute.data.csv.io.CsvFileReader
import org.bytebloom.logiroute.data.csv.io.CsvTablesName
import org.bytebloom.logiroute.data.csv.io.requireExpectedColumnCount
import org.bytebloom.logiroute.data.csv.io.requireNonBlankValues
import org.bytebloom.logiroute.data.csv.io.toDoubleOrThrow
import org.bytebloom.logiroute.data.csv.raw.VehicleRaw

class CsvVehicleDataSource(
    private val csvFileReader: CsvFileReader = CsvFileReader()
) : VehicleDataSource {

    companion object {
        private const val ID_INDEX = 0
        private const val HUB_INDEX = 1
        private const val CAPACITY_INDEX = 2
        private const val COST_INDEX = 3
    }

    override suspend fun loadAll(): List<VehicleRaw> =
        csvFileReader.loadCsv(fileName = CsvTablesName.FLEET, parser = ::parseVehicle).rows

    private fun parseVehicle(line: String, lineNumber: Int): VehicleRaw {
        val columns = line.split(",").map(String::trim)

        requireExpectedColumnCount(columns, CsvColumns.VEHICLE, lineNumber)

        val vehicleId = columns[ID_INDEX].uppercase()
        val currentHubId = columns[HUB_INDEX].uppercase()
        val capacityStr = columns[CAPACITY_INDEX]
        val costStr = columns[COST_INDEX]

        requireNonBlankValues(
            lineNumber,
            "Missing required vehicle identifiers or capacity/cost values.",
            vehicleId, currentHubId, capacityStr, costStr
        )

        val maxCapacityKg = capacityStr.toDoubleOrThrow("maximum capacity", lineNumber)
            ?: throw CsvParsingException("Line $lineNumber: maximum capacity field is required and cannot be empty.")

        val costPerKm = costStr.toDoubleOrThrow("cost per kilometer", lineNumber)
            ?: throw CsvParsingException("Line $lineNumber: cost per kilometer field is required and cannot be empty.")

        return VehicleRaw(
            id = vehicleId,
            currentWarehouseId = currentHubId,
            maxCapacityKg = maxCapacityKg,
            costPerKm = costPerKm
        )
    }
}