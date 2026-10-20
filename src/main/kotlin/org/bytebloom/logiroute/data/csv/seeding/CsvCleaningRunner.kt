package org.bytebloom.logiroute.data.csv.seeding

import kotlinx.coroutines.runBlocking
import org.bytebloom.logiroute.data.csv.datasource.CsvPackageDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvRouteDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvVehicleDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvWarehouseDataSource
import org.bytebloom.logiroute.data.csv.io.CsvFileWriter

fun main() = runBlocking {

    val cleaner = CsvDataCleaner(
        warehouseDataSource = CsvWarehouseDataSource(),
        routeDataSource = CsvRouteDataSource(),
        vehicleDataSource = CsvVehicleDataSource(),
        packageDataSource = CsvPackageDataSource(),
        csvFileWriter = CsvFileWriter()
    )

    cleaner.clean()

    println("CSV cleaning completed successfully.")
}