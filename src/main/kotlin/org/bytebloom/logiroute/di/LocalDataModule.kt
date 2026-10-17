package org.bytebloom.logiroute.di

import org.bytebloom.logiroute.data.local.cleaner.CsvWriter
import org.bytebloom.logiroute.data.local.common.CsvFileReader
import org.bytebloom.logiroute.data.local.csv.CsvPackageDataSource
import org.bytebloom.logiroute.data.local.csv.CsvRouteDataSource
import org.bytebloom.logiroute.data.local.csv.CsvVehicleDataSource
import org.bytebloom.logiroute.data.local.csv.CsvWarehouseDataSource
import org.bytebloom.logiroute.data.offline.CsvSnapshotStore
import org.bytebloom.logiroute.data.source.csv.PackageDataSource
import org.bytebloom.logiroute.data.source.csv.RouteDataSource
import org.bytebloom.logiroute.data.source.csv.VehicleDataSource
import org.bytebloom.logiroute.data.source.csv.WarehouseDataSource
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