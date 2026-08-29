package org.bytebloom.domain.usecase

import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle


class BestVehicleByCostCapacityUseCase (
    private val vehicleRepository: VehicleRepository
    ) {
    operator fun invoke(packages: List<Package>) : List<Vehicle> {
        val totalWeight = packages.sumOf {it.weight}
        val vehicles = vehicleRepository.getAll()
        val suitableVehicleByCapacity = vehicles.filter {it.maxCapacityKg >= totalWeight}
        val minCost = suitableVehicleByCapacity.minOfOrNull {it.costPerKm}
        val bestVehicleByCostAndCapacity = suitableVehicleByCapacity.filter {it.costPerKm == minCost}
        return bestVehicleByCostAndCapacity
    }
}
