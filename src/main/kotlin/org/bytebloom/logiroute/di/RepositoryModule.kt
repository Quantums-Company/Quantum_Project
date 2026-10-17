package org.bytebloom.logiroute.di

import org.bytebloom.logiroute.data.id.UuidIdGenerator
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.data.csv.repository.CsvPackageRepository
import org.bytebloom.logiroute.data.csv.repository.CsvRouteRepository
import org.bytebloom.logiroute.data.csv.repository.CsvVehicleRepository
import org.bytebloom.logiroute.data.csv.repository.CsvWarehouseRepository
import org.bytebloom.logiroute.data.offline.repository.OfflineFirstPackageRepository
import org.bytebloom.logiroute.data.offline.repository.OfflineFirstRouteRepository
import org.bytebloom.logiroute.data.offline.repository.OfflineFirstVehicleRepository
import org.bytebloom.logiroute.data.offline.repository.OfflineFirstWarehouseRepository
import org.bytebloom.logiroute.data.remote.repository.RemotePackageRepository
import org.bytebloom.logiroute.data.remote.repository.RemoteRouteRepository
import org.bytebloom.logiroute.data.remote.repository.RemoteVehicleRepository
import org.bytebloom.logiroute.data.remote.repository.RemoteWarehouseRepository
import org.bytebloom.logiroute.data.training.PackageHistoryTrainingCargoRepository
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.repository.TrainingCargoRepository
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.service.IdGenerator
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

    single<TrainingCargoRepository> {
        PackageHistoryTrainingCargoRepository(
            packageRepository = get()
        )
    }

    single<IdGenerator> {
        UuidIdGenerator()
    }
}