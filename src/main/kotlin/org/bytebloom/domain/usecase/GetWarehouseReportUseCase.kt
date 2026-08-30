package org.bytebloom.domain.usecase

import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.VehicleRepository

class GetWarehouseReportUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val packageRepository: PackageRepository,
    private val vehicleRepository: VehicleRepository
) {

    operator fun invoke(warehouseId: String): WarehouseReport? {

        val warehouse = warehouseRepository.getAll()
            .find { it.id == warehouseId }
            ?: return null
        val packages = packageRepository.getAll()
            .filter { it.originWarehouse.id == warehouseId }

        val vehicles = vehicleRepository.getAll()
            .filter { it.currentWarehouse.id == warehouseId }

        val packageCount = packages.size

        val totalPackageWeight =
            packages.sumOf { it.weight }

        val totalVehicleCapacity =
            vehicles.sumOf { it.maxCapacityKg }

        return WarehouseReport(
            warehouseId = warehouse.id,
            packageCount = packageCount,
            totalPackageWeight = totalPackageWeight,
            totalVehicleCapacity = totalVehicleCapacity
        )
    }
}
