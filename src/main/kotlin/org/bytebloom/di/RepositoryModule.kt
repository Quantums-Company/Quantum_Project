package org.bytebloom.di

import org.bytebloom.data.local.common.UuidIdGenerator
import org.bytebloom.data.repository.remote.RemotePackageRepository
import org.bytebloom.data.repository.remote.RemoteRouteRepository
import org.bytebloom.data.repository.remote.RemoteVehicleRepository
import org.bytebloom.data.repository.remote.RemoteWarehouseRepository
import org.bytebloom.data.repository.training.PackageHistoryTrainingCargoRepository
import org.bytebloom.domain.repository.*
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
    single<TrainingCargoRepository> {
        PackageHistoryTrainingCargoRepository(
            packageRepository = get()
        )
    }
    single<IdGenerator> {
        UuidIdGenerator()
    }
}