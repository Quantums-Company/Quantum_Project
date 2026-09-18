package org.bytebloom.data.local.cleaner

import kotlinx.coroutines.runBlocking
import org.bytebloom.data.local.CsvPackageDataSource
import org.bytebloom.data.local.CsvRouteDataSource
import org.bytebloom.data.local.CsvVehicleDataSource
import org.bytebloom.data.local.CsvWarehouseDataSource

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