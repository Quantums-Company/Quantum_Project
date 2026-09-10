package org.bytebloom.presentation

import org.bytebloom.data.repository.CsvPackageRepository
import org.bytebloom.data.repository.CsvRouteRepository
import org.bytebloom.data.repository.CsvVehicleRepository
import org.bytebloom.data.repository.CsvWarehouseRepository
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.data.local.csv.CsvPackageDataSource
import org.bytebloom.data.local.csv.CsvRouteDataSource
import org.bytebloom.data.local.csv.CsvVehicleDataSource
import org.bytebloom.data.local.csv.CsvWarehouseDataSource

fun main() {
    val warehouseRepo: WarehouseRepository =
        CsvWarehouseRepository(CsvWarehouseDataSource())

    val warehousesById =
        warehouseRepo
            .getAll()
            .associateBy { it.id }

    val packageRepo: PackageRepository =
        CsvPackageRepository(warehousesById, CsvPackageDataSource())

    val routeRepo: RouteRepository =
        CsvRouteRepository(warehousesById, CsvRouteDataSource())

    val vehicleRepo: VehicleRepository =
        CsvVehicleRepository(warehousesById, CsvVehicleDataSource())

    DemoRunner(
        warehouseRepository = warehouseRepo,
        packageRepository = packageRepo,
        routeRepository = routeRepo,
        vehicleRepository = vehicleRepo
    ).run()
}

