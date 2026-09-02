package org.bytebloom.domain.usecase.queries.reporting

import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle

class GetWarehouseReportUseCase(
    private val warehouseRepository: WarehouseRepository,
) {

    operator fun invoke(
        warehouseId: String
    ): WarehouseReport? {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        val warehouse = warehousesById[warehouseId]?: return null

        val packages = warehouse.cargoQueue

        val vehicles = warehouse.stationedVehicles

        return WarehouseReport(
            warehouseId = warehouse.id,
            packageCount = packages.size,
            totalPackageWeight = packages.sumOf(Package::weight),
            totalVehicleCapacity = vehicles.sumOf(Vehicle::maxCapacityKg)
        )
    }
}
