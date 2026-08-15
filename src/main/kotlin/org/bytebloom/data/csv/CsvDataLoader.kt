package org.bytebloom.data.csv

import org.bytebloom.data.csv.perser.parsePackage
import org.bytebloom.data.csv.perser.parseRoute
import org.bytebloom.data.csv.perser.parseVehicle
import org.bytebloom.data.csv.perser.parseWarehouse
import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.data.raw.VehicleRaw
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.util.Logger
import java.io.File
import java.io.IOException

const val RESOURCE_PATH = "src/resources"

fun loadPackages(): List<PackageRaw>  = loadCsv(CsvTablesName.PACKAGE, ::parsePackage)

fun loadVehicles(): List<VehicleRaw> = loadCsv(CsvTablesName.FLEET, ::parseVehicle)

fun loadRoutes(): List<RouteRaw> = loadCsv(CsvTablesName.ROUTE, ::parseRoute)

fun loadWarehouses(): List<WarehouseRaw> = loadCsv(CsvTablesName.WAREHOUSE, ::parseWarehouse)

private fun <T> loadCsv(
    fileName:String,
    parser:(String,Int)->T?
):List<T>{
    val raws = mutableListOf<T>()

    loadCsvFile(fileName) { line, lineNumber ->
        parser(line, lineNumber)?.let(raws::add)
    }

    Logger.info("Successfully parsed routes: ${raws.size}")
    return raws
}

private fun loadCsvFile(
    fileName: String,
    processLine: (String, Int) -> Unit
) {
    val file = File(RESOURCE_PATH, fileName)

    if (!file.exists()) {
        Logger.warning("File '$fileName' was not found!")
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