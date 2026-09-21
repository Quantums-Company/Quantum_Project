package org.bytebloom.data.local.cleaner

import kotlinx.coroutines.runBlocking
import org.bytebloom.data.local.csv.CsvPackageDataSource
import org.bytebloom.data.local.csv.CsvRouteDataSource
import org.bytebloom.data.local.csv.CsvVehicleDataSource
import org.bytebloom.data.local.csv.CsvWarehouseDataSource
import org.bytebloom.data.remote.client.SupabaseClientProvider

object CsvUploadRunner {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val uploader = CsvToSupabaseUploader(
            client = SupabaseClientProvider.create(),
            warehouseDataSource = CsvWarehouseDataSource(),
            routeDataSource = CsvRouteDataSource(),
            vehicleDataSource = CsvVehicleDataSource(),
            packageDataSource = CsvPackageDataSource()
        )

        uploader.upload()
    }
}