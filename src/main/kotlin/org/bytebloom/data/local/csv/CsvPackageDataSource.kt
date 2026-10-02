package org.bytebloom.data.local.csv

import org.bytebloom.data.exception.CsvParsingException
import org.bytebloom.data.local.common.CsvColumns
import org.bytebloom.data.local.common.CsvFileReader
import org.bytebloom.data.local.common.CsvTablesName
import org.bytebloom.data.local.common.requireExpectedColumnCount
import org.bytebloom.data.local.common.requireNonBlankValues
import org.bytebloom.data.local.common.toDoubleOrThrow
import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.data.source.csv.PackageDataSource
import org.bytebloom.domain.model.Priority

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
        csvFileReader.loadCsv(fileName = CsvTablesName.PACKAGE, parser = ::parsePackage).rows

    private fun parsePackage(line: String, lineNumber: Int): PackageRaw {
        val columns = line.split(",").map(String::trim)

        requireExpectedColumnCount(columns, CsvColumns.PACKAGE, lineNumber)

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
    } catch (_: Exception) {
        throw CsvParsingException("Line $line: invalid priority value '$value'.")
    }

    // Read-only CSV sources throw UnsupportedOperationException for write operations
    override suspend fun getById(): List<PackageRaw> =
        throw UnsupportedOperationException("CSV Data Source is read-only.")

    override suspend fun create(): List<PackageRaw> =
        throw UnsupportedOperationException("CSV Data Source is read-only.")

    override suspend fun update(): List<PackageRaw> =
        throw UnsupportedOperationException("CSV Data Source is read-only.")

    override suspend fun delete(): List<PackageRaw> =
        throw UnsupportedOperationException("CSV Data Source is read-only.")
}