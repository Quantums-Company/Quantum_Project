package org.bytebloom.domain.usecase

import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle

class GetWarehouseReportUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val packageRepository: PackageRepository,
    private val vehicleRepository: VehicleRepository
) {

    operator fun invoke(
        warehouseId: String
    ): WarehouseReport? {

        val warehouse =
            warehouseRepository
                .getAll()
                .firstOrNull { it.id == warehouseId }
                ?: return null

        val packages =
            packageRepository
                .getAll()
                .asSequence()
                .filter { it.originWarehouse.id == warehouseId }
                .toList()

        val vehicles =
            vehicleRepository
                .getAll()
                .asSequence()
                .filter { it.currentWarehouse.id == warehouseId }
                .toList()

        return WarehouseReport(
            warehouseId = warehouse.id,
            packageCount = packages.size,
            totalPackageWeight = packages.sumOf(Package::weight),
            totalVehicleCapacity = vehicles.sumOf(Vehicle::maxCapacityKg)
        )
    }
}
