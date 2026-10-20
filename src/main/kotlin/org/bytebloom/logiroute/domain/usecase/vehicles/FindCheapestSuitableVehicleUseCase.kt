package org.bytebloom.logiroute.domain.usecase.vehicles

import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle

class FindCheapestSuitableVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {

    suspend operator fun invoke(
        packages: List<Package>
    ): Vehicle? {

        val requiredCapacityKg =
            packages.sumOf(Package::weight)

        return vehicleRepository
            .getAll()
            .asSequence()
            .filter { it.maxCapacityKg >= requiredCapacityKg }
            .minByOrNull(Vehicle::costPerKm)
    }
}
