package org.bytebloom.di

import org.bytebloom.data.local.cleaner.CsvWriter
import org.bytebloom.data.local.common.CsvFileReader
import org.bytebloom.data.local.csv.CsvPackageDataSource
import org.bytebloom.data.local.csv.CsvRouteDataSource
import org.bytebloom.data.local.csv.CsvVehicleDataSource
import org.bytebloom.data.local.csv.CsvWarehouseDataSource
import org.bytebloom.data.offline.CsvSnapshotStore
import org.bytebloom.data.source.csv.PackageDataSource
import org.bytebloom.data.source.csv.RouteDataSource
import org.bytebloom.data.source.csv.VehicleDataSource
import org.bytebloom.data.source.csv.WarehouseDataSource
import org.koin.dsl.module
import java.io.File

val localDataModule = module {
    single { CsvFileReader() }
    single { CsvWriter() }

    single {
        CsvSnapshotStore(
            writer = get(),
            cacheDirectory = File(CsvFileReader.CACHE_DIRECTORY)
        )
    }

    single<WarehouseDataSource> {
        CsvWarehouseDataSource(get())
    }

    single<PackageDataSource> {
        CsvPackageDataSource(get())
    }

    single<RouteDataSource> {
        CsvRouteDataSource(get())
    }

    single<VehicleDataSource> {
        CsvVehicleDataSource(get())
    }
}