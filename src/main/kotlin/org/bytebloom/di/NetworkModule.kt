package org.bytebloom.di

import io.github.jan.supabase.SupabaseClient
import org.bytebloom.data.remote.client.SupabaseClientProvider
import org.bytebloom.data.remote.datasource.SdkPackageDataSource
import org.bytebloom.data.remote.datasource.SdkRouteDataSource
import org.bytebloom.data.remote.datasource.SdkVehicleDataSource
import org.bytebloom.data.remote.datasource.SdkWarehouseDataSource
import org.bytebloom.data.source.remote.PackageRemoteDataSource
import org.bytebloom.data.source.remote.RouteRemoteDataSource
import org.bytebloom.data.source.remote.VehicleRemoteDataSource
import org.bytebloom.data.source.remote.WarehouseRemoteDataSource
import org.koin.dsl.module

val networkModule = module {

    single<SupabaseClient> {
        SupabaseClientProvider.create()
    }

    single<WarehouseRemoteDataSource> {
        SdkWarehouseDataSource(get())
    }

    single<PackageRemoteDataSource> {
        SdkPackageDataSource(get())
    }

    single<RouteRemoteDataSource> {
        SdkRouteDataSource(get())
    }

    single<VehicleRemoteDataSource> {
        SdkVehicleDataSource(get())
    }
}