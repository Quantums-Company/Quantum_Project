package org.bytebloom.logiroute.di

import io.github.jan.supabase.SupabaseClient
import org.bytebloom.logiroute.data.remote.client.SupabaseClientProvider
import org.bytebloom.logiroute.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.logiroute.data.remote.datasource.PackageRemoteDataSource
import org.bytebloom.logiroute.data.remote.datasource.RouteRemoteDataSource
import org.bytebloom.logiroute.data.remote.datasource.SdkPackageDataSource
import org.bytebloom.logiroute.data.remote.datasource.SdkRouteDataSource
import org.bytebloom.logiroute.data.remote.datasource.SdkVehicleDataSource
import org.bytebloom.logiroute.data.remote.datasource.SdkWarehouseDataSource
import org.bytebloom.logiroute.data.remote.datasource.VehicleRemoteDataSource
import org.bytebloom.logiroute.data.remote.datasource.WarehouseRemoteDataSource
import org.bytebloom.logiroute.data.remote.resilience.CircuitBreaker
import org.bytebloom.logiroute.data.remote.util.RateLimiter
import org.bytebloom.logiroute.data.remote.util.RemoteExecutor
import org.koin.dsl.module

val networkModule = module {
    single { SupabaseClientProvider() }
    single { SupabaseErrorTranslator() }

    single { RateLimiter() }
    single { CircuitBreaker() }

    single {
        RemoteExecutor(
            errorTranslator = get(),
            rateLimiter = get(),
            circuitBreaker = get()
        )
    }

    single<SupabaseClient> {
        get<SupabaseClientProvider>().create()
    }

    single<WarehouseRemoteDataSource> {
        SdkWarehouseDataSource(client = get(), remoteExecutor = get())
    }

    single<PackageRemoteDataSource> {
        SdkPackageDataSource(client = get(), remoteExecutor = get())
    }

    single<RouteRemoteDataSource> {
        SdkRouteDataSource(client = get(), remoteExecutor = get())
    }

    single<VehicleRemoteDataSource> {
        SdkVehicleDataSource(client = get(), remoteExecutor = get())
    }
}
