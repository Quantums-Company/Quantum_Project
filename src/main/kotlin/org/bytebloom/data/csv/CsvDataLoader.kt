package org.bytebloom.data.csv

import org.bytebloom.data.csv.parser.parsePackage
import org.bytebloom.data.csv.parser.parseRoute
import org.bytebloom.data.csv.parser.parseVehicle
import org.bytebloom.data.csv.parser.parseWarehouse
import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.data.raw.VehicleRaw
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.util.Logger
import java.io.File
import java.io.IOException

const val DEFAULT_CSV_DIRECTORY = "src/resources"

fun loadPackages(csvDirectory: String = DEFAULT_CSV_DIRECTORY): List<PackageRaw> =
    loadCsv(csvDirectory, CsvTablesName.PACKAGE, ::parsePackage)

fun loadVehicles(csvDirectory: String = DEFAULT_CSV_DIRECTORY): List<VehicleRaw> =
    loadCsv(csvDirectory, CsvTablesName.FLEET, ::parseVehicle)

fun loadRoutes(csvDirectory: String = DEFAULT_CSV_DIRECTORY): List<RouteRaw> =
    loadCsv(csvDirectory, CsvTablesName.ROUTE, ::parseRoute)

fun loadWarehouses(csvDirectory: String = DEFAULT_CSV_DIRECTORY): List<WarehouseRaw> =
    loadCsv(csvDirectory, CsvTablesName.WAREHOUSE, ::parseWarehouse)

private fun <T> loadCsv(
    csvDirectory: String,
    fileName: String,
    parser: (String, Int) -> T?
): List<T> {
    val raws = mutableListOf<T>()

    loadCsvFile(csvDirectory, fileName) { line, lineNumber ->
        parser(line, lineNumber)?.let(raws::add)
    }

    Logger.info("Successfully parsed $fileName: ${raws.size} row(s).")
    return raws
}

private fun loadCsvFile(
    csvDirectory: String,
    fileName: String,
    processLine: (String, Int) -> Unit
) {
    val file = File(csvDirectory, fileName)

    if (!file.exists()) {
        Logger.warning("File '$fileName' was not found in '$csvDirectory'.")
        return
    }

    try {
        file.useLines { lines ->
            lines.drop(1).forEachIndexed { index, line ->
                if (line.isNotBlank()) {
                    processLine(line, index + 2)
                }
            }
        }
    } catch (e: IOException) {
        Logger.error("reading file '$fileName': ${e.message}")
    }
}