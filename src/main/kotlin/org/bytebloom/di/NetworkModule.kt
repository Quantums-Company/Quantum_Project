package org.bytebloom.di

import io.github.jan.supabase.SupabaseClient
import org.bytebloom.data.remote.client.SupabaseClientProvider
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.data.remote.datasource.SdkPackageDataSource
import org.bytebloom.data.remote.datasource.SdkRouteDataSource
import org.bytebloom.data.remote.datasource.SdkVehicleDataSource
import org.bytebloom.data.remote.datasource.SdkWarehouseDataSource
import org.bytebloom.data.remote.util.RemoteExecutor
import org.bytebloom.data.source.remote.PackageRemoteDataSource
import org.bytebloom.data.source.remote.RouteRemoteDataSource
import org.bytebloom.data.source.remote.VehicleRemoteDataSource
import org.bytebloom.data.source.remote.WarehouseRemoteDataSource
import org.koin.dsl.module
import org.bytebloom.data.remote.util.RateLimiter

val networkModule = module {
    single { SupabaseClientProvider() }
    single { SupabaseErrorTranslator() }

    single { RateLimiter() }
    single { RemoteExecutor(errorTranslator = get(), rateLimiter = get()) }
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