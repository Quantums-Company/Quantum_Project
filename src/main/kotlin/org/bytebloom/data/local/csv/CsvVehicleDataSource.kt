package org.bytebloom.data.local.csv

import org.bytebloom.data.local.common.CsvColumns
import org.bytebloom.data.local.common.CsvFileReader
import org.bytebloom.data.local.common.CsvTablesName
import org.bytebloom.data.local.common.hasExpectedColumns
import org.bytebloom.data.local.common.hasRequiredValues
import org.bytebloom.data.local.common.toValidDouble
import org.bytebloom.data.raw.VehicleRaw
import org.bytebloom.data.source.csv.VehicleDataSource

class CsvVehicleDataSource: VehicleDataSource {
    companion object {
        private const val ID_INDEX = 0
        private const val HUB_INDEX = 1
        private const val CAPACITY_INDEX = 2
        private const val COST_INDEX = 3
    }

    val csvFileReader = CsvFileReader()

    fun parseVehicle(line: String, lineNumber: Int): VehicleRaw? {
        val columns = line.split(",").map(String::trim)

        return columns.takeIf { hasExpectedColumns(it, CsvColumns.VEHICLE, lineNumber) }
            ?.let { extractVehicleFromColumns(it, lineNumber) }
    }

    private fun extractVehicleFromColumns(
        columns: List<String>,
        lineNumber: Int
    ): VehicleRaw? {
        val vehicleId = columns[ID_INDEX].trim().uppercase()
        val currentHubId = columns[HUB_INDEX].trim().uppercase()
        val maxCapacityKg = columns[CAPACITY_INDEX].toValidDouble("maximum capacity", lineNumber)
        val costPerKm = columns[COST_INDEX].toValidDouble("cost per kilometer", lineNumber)

        return if (hasRequiredValues(lineNumber, "Missing required vehicle data.", vehicleId, currentHubId)
            && maxCapacityKg != null && costPerKm != null
        ) {
            VehicleRaw(
                id = vehicleId,
                currentWarehouseId = currentHubId,
                maxCapacityKg = maxCapacityKg,
                costPerKm = costPerKm
            )
        } else {
            null
        }
    }

    override suspend fun loadAll(): List<VehicleRaw> =
        csvFileReader.loadCsv(fileName = CsvTablesName.FLEET, parser = ::parseVehicle)

    override suspend fun getById(): List<VehicleRaw> {
        TODO("Not yet implemented")
    }

    override suspend fun create(): List<VehicleRaw> {
        TODO("Not yet implemented")
    }

    override suspend fun update(): List<VehicleRaw> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(): List<VehicleRaw> {
        TODO("Not yet implemented")
    }
}