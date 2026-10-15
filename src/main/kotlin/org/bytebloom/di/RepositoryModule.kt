package org.bytebloom.di

import org.bytebloom.data.local.common.UuidIdGenerator
import org.bytebloom.data.offline.OfflineReadExecutor
import org.bytebloom.data.repository.csv.CsvPackageRepository
import org.bytebloom.data.repository.csv.CsvRouteRepository
import org.bytebloom.data.repository.csv.CsvVehicleRepository
import org.bytebloom.data.repository.csv.CsvWarehouseRepository
import org.bytebloom.data.repository.offline.OfflineFirstPackageRepository
import org.bytebloom.data.repository.offline.OfflineFirstRouteRepository
import org.bytebloom.data.repository.offline.OfflineFirstVehicleRepository
import org.bytebloom.data.repository.offline.OfflineFirstWarehouseRepository
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

    // Remote implementations
    single { RemoteWarehouseRepository(get()) }
    single { RemotePackageRepository(get(), get()) }
    single { RemoteRouteRepository(get(), get()) }
    single { RemoteVehicleRepository(get(), get()) }

    // Local CSV implementations
    single { CsvWarehouseRepository(get()) }

    single {
        CsvPackageRepository(
            warehouseRepository = get(),
            csvPackageDataSource = get()
        )
    }

    single {
        CsvRouteRepository(
            warehouseRepository = get(),
            csvRouteDataSource = get()
        )
    }

    single {
        CsvVehicleRepository(
            warehouseRepository = get(),
            csvVehicleDataSource = get()
        )
    }

    single { OfflineReadExecutor() }

    // Domain interfaces use Offline-first implementations
    single<WarehouseRepository> {
        OfflineFirstWarehouseRepository(
            remote = get(),
            local = get(),
            snapshots = get(),
            executor = get()
        )
    }

    single<PackageRepository> {
        OfflineFirstPackageRepository(
            remote = get(),
            local = get(),
            snapshots = get(),
            executor = get()
        )
    }

    single<RouteRepository> {
        OfflineFirstRouteRepository(
            remote = get(),
            local = get(),
            snapshots = get(),
            executor = get()
        )
    }

    single<VehicleRepository> {
        OfflineFirstVehicleRepository(
            remote = get(),
            local = get(),
            snapshots = get(),
            executor = get()
        )
    }

    single<IdGenerator> {
        UuidIdGenerator()
    }
}