package org.bytebloom.logiroute.di

import org.bytebloom.logiroute.data.csv.io.CsvFileWriter
import org.bytebloom.logiroute.data.csv.io.CsvFileReader
import org.bytebloom.logiroute.data.csv.datasource.CsvPackageDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvRouteDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvVehicleDataSource
import org.bytebloom.logiroute.data.csv.datasource.CsvWarehouseDataSource
import org.bytebloom.logiroute.data.csv.cache.CsvSnapshotStore
import org.bytebloom.logiroute.data.csv.datasource.PackageDataSource
import org.bytebloom.logiroute.data.csv.datasource.RouteDataSource
import org.bytebloom.logiroute.data.csv.datasource.VehicleDataSource
import org.bytebloom.logiroute.data.csv.datasource.WarehouseDataSource
import org.koin.dsl.module
import java.io.File

val localDataModule = module {
    single { CsvFileReader() }
    single { CsvFileWriter() }

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