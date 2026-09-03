package org.bytebloom.presentation

import org.bytebloom.data.repository.CsvPackageRepository
import org.bytebloom.data.repository.CsvRouteRepository
import org.bytebloom.data.repository.CsvVehicleRepository
import org.bytebloom.data.repository.CsvWarehouseRepository
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository

fun main() {
    val warehouseRepo: WarehouseRepository =
        CsvWarehouseRepository()

    val warehousesById =
        warehouseRepo
            .getAll()
            .associateBy { it.id }

    val packageRepo: PackageRepository =
        CsvPackageRepository(warehousesById)

    val routeRepo: RouteRepository =
        CsvRouteRepository(warehousesById)

    val vehicleRepo: VehicleRepository =
        CsvVehicleRepository(warehousesById)

    DemoRunner(
        warehouseRepository = warehouseRepo,
        packageRepository = packageRepo,
        routeRepository = routeRepo,
        vehicleRepository = vehicleRepo
    ).run()
}

