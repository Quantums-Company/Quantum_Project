package org.bytebloom.domain.usecase

import NetworkStatistics
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository



class GetNetworkStatisticsUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val packageRepository: PackageRepository,
    private val vehicleRepository: VehicleRepository
) {

    operator fun invoke(): NetworkStatistics {
        val warehouses = warehouseRepository.getAll()
        val packages = packageRepository.getAll()
        val vehicles = vehicleRepository.getAll()

        return NetworkStatistics(
            warehouseCount = warehouses.size,
            packageCount = packages.size,
            vehicleCount = vehicles.size,
            totalPackageWeight = packages.sumOf { it.weight },
            totalVehicleCapacity = vehicles.sumOf { it.maxCapacityKg }
        )
    }
}
