package org.bytebloom.data.csv

import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.data.raw.VehicleRaw
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.util.Logger
import java.io.File
import java.io.IOException

const val RESOURCE_PATH = "src/main/kotlin/org/bytebloom/data/resources"

object CsvColumns {
    const val PACKAGE = 5
    const val ROUTE = 5
    const val VEHICLE = 4
    const val WAREHOUSE = 5
}

fun loadPackages(fileName: String): List<PackageRaw>  = loadCsv(fileName, ::parsePackage)

fun loadVehicles(fileName: String): List<VehicleRaw> = loadCsv(fileName, ::parseVehicle)

fun loadRoutes(fileName: String): List<RouteRaw> = loadCsv(fileName, ::parseRoute)

fun loadWarehouses(fileName: String): List<WarehouseRaw> = loadCsv(fileName, ::parseWarehouse)

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