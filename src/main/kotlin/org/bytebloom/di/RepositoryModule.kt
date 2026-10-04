package org.bytebloom.di

import org.bytebloom.data.local.common.UuidIdGenerator
import org.bytebloom.data.repository.remote.RemotePackageRepository
import org.bytebloom.data.repository.remote.RemoteRouteRepository
import org.bytebloom.data.repository.remote.RemoteVehicleRepository
import org.bytebloom.data.repository.remote.RemoteWarehouseRepository
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.service.IdGenerator
import org.koin.dsl.module

val repositoryModule = module {

    single<WarehouseRepository> {
        RemoteWarehouseRepository(
            remoteDataSource = get()
        )
    }

    single<PackageRepository> {
        RemotePackageRepository(
            warehouseRepository = get(),
            remoteDataSource = get()
        )
    }

    single<RouteRepository> {
        RemoteRouteRepository(
            warehouseRepository = get(),
            remoteDataSource = get()
        )
    }

    single<VehicleRepository> {
        RemoteVehicleRepository(
            warehouseRepository = get(),
            remoteDataSource = get()
        )
    }

    single<IdGenerator> {
        UuidIdGenerator()
    }
}