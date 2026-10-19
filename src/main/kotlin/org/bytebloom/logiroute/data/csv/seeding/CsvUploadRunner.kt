package org.bytebloom.logiroute.data.csv.seeding

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.data.csv.datasource.CsvPackageDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvRouteDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvVehicleDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvWarehouseDataSource
import org.bytebloom.logiroute.data.remote.client.SupabaseClientProvider

@Suppress("TooGenericExceptionCaught")
fun main() = runBlocking {
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
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        println("❌ Failed to upload CSV data: ${e.message}")
        e.printStackTrace()
    }
}