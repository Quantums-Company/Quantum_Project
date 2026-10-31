package org.bytebloom.logiroute.data.csv.datasource

import org.bytebloom.logiroute.data.csv.exception.CsvParsingException
import org.bytebloom.logiroute.data.csv.io.CsvFileReader
import org.bytebloom.logiroute.data.csv.io.requireExpectedColumnCount
import org.bytebloom.logiroute.data.csv.io.requireNonBlankValues
import org.bytebloom.logiroute.data.csv.io.toDoubleOrThrow
import org.bytebloom.logiroute.data.csv.raw.PackageRaw
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.data.csv.io.CsvTable

class CsvPackageDataSource(
    private val csvFileReader: CsvFileReader = CsvFileReader()
) : PackageDataSource {

    companion object {
        private const val ID_INDEX = 0
        private const val WEIGHT_INDEX = 1
        private const val ORIGIN_INDEX = 2
        private const val DESTINATION_INDEX = 3
        private const val PRIORITY_INDEX = 4
    }

    override suspend fun loadAll(): List<PackageRaw> =
        csvFileReader.loadCsv(fileName = CsvTable.PACKAGES.fileName, parser = ::parsePackage).rows

    private fun parsePackage(line: String, lineNumber: Int): PackageRaw {
        val columns = line.split(",").map(String::trim)

        requireExpectedColumnCount(columns, CsvTable.PACKAGES.columnCount, lineNumber)

        val id = columns[ID_INDEX].uppercase()
        val weightValue = columns[WEIGHT_INDEX]
        val originHubId = columns[ORIGIN_INDEX].uppercase()
        val destinationHubId = columns[DESTINATION_INDEX].uppercase()
        val priorityValue = columns[PRIORITY_INDEX]

        requireNonBlankValues(
            lineNumber,
            "Missing required package identifier or warehouse IDs.",
            id, originHubId, destinationHubId, weightValue, priorityValue
        )

        val weight = weightValue.toDoubleOrThrow("weight", lineNumber)
            ?: throw CsvParsingException("Line $lineNumber: weight field is required and cannot be empty.")

        val priority = parsePriority(priorityValue, lineNumber)

        return PackageRaw(
            id = id,
            weight = weight,
            originWarehouseId = originHubId,
            destinationWarehouseId = destinationHubId,
            priority = priority
        )
    }

    private fun parsePriority(value: String, line: Int): Priority = try {
        Priority.valueOf(value.uppercase())
    } catch (_: IllegalArgumentException) {
        throw CsvParsingException("Line $line: invalid priority value '$value'.")
    }
}