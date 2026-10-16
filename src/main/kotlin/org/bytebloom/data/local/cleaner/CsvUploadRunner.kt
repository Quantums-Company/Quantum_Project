package org.bytebloom.data.local.cleaner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bytebloom.data.local.csv.CsvPackageDataSource
import org.bytebloom.data.local.csv.CsvRouteDataSource
import org.bytebloom.data.local.csv.CsvVehicleDataSource
import org.bytebloom.data.local.csv.CsvWarehouseDataSource
import org.bytebloom.data.remote.client.SupabaseClientProvider

object CsvUploadRunner {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println("🚀 Starting CSV to Supabase upload process...")

        try {
            withContext(Dispatchers.IO) {
                val supabaseClient = SupabaseClientProvider().create()

                val uploader = CsvToSupabaseUploader(
                    client = supabaseClient,
                    warehouseDataSource = CsvWarehouseDataSource(),
                    routeDataSource = CsvRouteDataSource(),
                    vehicleDataSource = CsvVehicleDataSource(),
                    packageDataSource = CsvPackageDataSource()
                )

                uploader.upload()
            }
            println("✅ CSV Data uploaded successfully to Supabase!")
        } catch (e: Exception) {
            println("❌ Failed to upload CSV data: ${e.message}")
            e.printStackTrace()
        }
    }
}