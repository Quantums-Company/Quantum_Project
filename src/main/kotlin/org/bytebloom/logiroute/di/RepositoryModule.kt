package org.bytebloom.logiroute.di

import org.bytebloom.logiroute.data.id.UuidIdGenerator
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.data.csv.repository.CsvPackageRepository
import org.bytebloom.logiroute.data.csv.repository.CsvRouteRepository
import org.bytebloom.logiroute.data.csv.repository.CsvVehicleRepository
import org.bytebloom.logiroute.data.csv.repository.CsvWarehouseRepository
import org.bytebloom.logiroute.data.repository.offline.OfflineFirstPackageRepository
import org.bytebloom.logiroute.data.repository.offline.OfflineFirstRouteRepository
import org.bytebloom.logiroute.data.repository.offline.OfflineFirstVehicleRepository
import org.bytebloom.logiroute.data.repository.offline.OfflineFirstWarehouseRepository
import org.bytebloom.logiroute.data.repository.remote.RemotePackageRepository
import org.bytebloom.logiroute.data.repository.remote.RemoteRouteRepository
import org.bytebloom.logiroute.data.repository.remote.RemoteVehicleRepository
import org.bytebloom.logiroute.data.repository.remote.RemoteWarehouseRepository
import org.bytebloom.logiroute.data.repository.training.PackageHistoryTrainingCargoRepository
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