package org.bytebloom.logiroute.di

import io.github.jan.supabase.SupabaseClient
import org.bytebloom.logiroute.data.remote.client.SupabaseClientProvider
import org.bytebloom.logiroute.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.logiroute.data.remote.datasource.SdkPackageDataSource
import org.bytebloom.logiroute.data.remote.datasource.SdkRouteDataSource
import org.bytebloom.logiroute.data.remote.datasource.SdkVehicleDataSource
import org.bytebloom.logiroute.data.remote.datasource.SdkWarehouseDataSource
import org.bytebloom.logiroute.data.remote.util.RemoteExecutor
import org.bytebloom.logiroute.data.source.remote.PackageRemoteDataSource
import org.bytebloom.logiroute.data.source.remote.RouteRemoteDataSource
import org.bytebloom.logiroute.data.source.remote.VehicleRemoteDataSource
import org.bytebloom.logiroute.data.source.remote.WarehouseRemoteDataSource
import org.koin.dsl.module
import org.bytebloom.logiroute.data.remote.util.RateLimiter

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