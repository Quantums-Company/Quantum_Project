package org.bytebloom.logiroute.data.csv.seeding

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.bytebloom.logiroute.data.csv.datasource.CsvPackageDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvRouteDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvVehicleDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvWarehouseDataSource
import org.bytebloom.logiroute.data.remote.client.SupabaseClientProvider
import org.bytebloom.logiroute.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.logiroute.data.remote.resilience.CircuitBreaker
import org.bytebloom.logiroute.data.remote.util.RateLimiter
import org.bytebloom.logiroute.data.remote.util.RemoteExecutor

@Suppress("TooGenericExceptionCaught")
fun main() = runBlocking {
    println("🚀 Starting CSV to Supabase upload process...")

    try {
        val supabaseClient = SupabaseClientProvider().create()
        val remoteExecutor = RemoteExecutor(
            errorTranslator = SupabaseErrorTranslator(),
            rateLimiter = RateLimiter(),
            circuitBreaker = CircuitBreaker()
        )

        val uploader = CsvToSupabaseUploader(
            client = supabaseClient,
            remoteExecutor = remoteExecutor,
            warehouseDataSource = CsvWarehouseDataSource(),
            routeDataSource = CsvRouteDataSource(),
            vehicleDataSource = CsvVehicleDataSource(),
            packageDataSource = CsvPackageDataSource()
        )

        uploader.upload()
        println("✅ CSV Data uploaded successfully to Supabase!")
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        println("❌ Failed to upload CSV data: ${e.message}")
        e.printStackTrace()
    }
}