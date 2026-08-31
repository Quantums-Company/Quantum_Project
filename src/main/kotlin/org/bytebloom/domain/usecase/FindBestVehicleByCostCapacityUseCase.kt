package org.bytebloom.domain.usecase

import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle

class FindBestVehicleByCostCapacityUseCase(
    private val vehicleRepository: VehicleRepository
) {

    operator fun invoke(
        packages: List<Package>
    ): Vehicle? {

        val requiredCapacity =
            packages.sumOf(Package::weight)

        return vehicleRepository
            .getAll()
            .asSequence()
            .filter { it.maxCapacityKg >= requiredCapacity }
            .minByOrNull(Vehicle::costPerKm)
    }
}
