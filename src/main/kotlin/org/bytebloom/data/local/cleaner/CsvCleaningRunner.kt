package org.bytebloom.data.local.cleaner

import kotlinx.coroutines.runBlocking
import org.bytebloom.data.local.csv.CsvPackageDataSource
import org.bytebloom.data.local.csv.CsvRouteDataSource
import org.bytebloom.data.local.csv.CsvVehicleDataSource
import org.bytebloom.data.local.csv.CsvWarehouseDataSource

object CsvCleaningRunner {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val cleaner = CsvDataCleaner(
            warehouseDataSource = CsvWarehouseDataSource(),
            routeDataSource = CsvRouteDataSource(),
            vehicleDataSource = CsvVehicleDataSource(),
            packageDataSource = CsvPackageDataSource(),
            csvWriter = CsvWriter()
        )

        cleaner.clean()

        println("CSV cleaning completed successfully.")
    }
}