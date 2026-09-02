package org.bytebloom.domain.usecase.queries.backhaul

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository

class FindBackhaulOpportunityUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(vehicle: Vehicle, destinationWarehouse: Warehouse): BackhaulOpportunity? {
        val returnPackages = findEligibleReturnPackages(vehicle, destinationWarehouse)
        val selectedPackages = selectPackagesWithinCapacity(returnPackages, vehicle.maxCapacityKg)

        if (selectedPackages.isEmpty()) return null

        return createBackhaulOpportunity(vehicle, destinationWarehouse, selectedPackages)
    }

    private fun findEligibleReturnPackages(vehicle: Vehicle, destinationWarehouse: Warehouse): List<Package> {
        return packageRepository.getAll().filter { pkg ->
            isMatchingRoute(pkg, origin = destinationWarehouse.id, destination = vehicle.currentWarehouse.id)
        }
    }

    private fun isMatchingRoute(pkg: Package, origin: String, destination: String): Boolean {
        val startsAtDestination = pkg.originWarehouse.id.equals(origin, ignoreCase = true)
        val endsAtCurrentLocation = pkg.destinationWarehouse.id.equals(destination, ignoreCase = true)
        return startsAtDestination && endsAtCurrentLocation
    }

    private fun selectPackagesWithinCapacity(packages: List<Package>, capacityKg: Double): List<Package> {
        val selectedPackages = mutableListOf<Package>()
        var accumulatedWeight = 0.0

        for (pkg in packages) {
            if (accumulatedWeight + pkg.weight > capacityKg) continue

            selectedPackages.add(pkg)
            accumulatedWeight += pkg.weight
        }

        return selectedPackages
    }

    private fun createBackhaulOpportunity(
        vehicle: Vehicle,
        destinationWarehouse: Warehouse,
        selectedPackages: List<Package>
    ): BackhaulOpportunity {
        val totalCargoWeight = selectedPackages.sumOf(Package::weight)

        return BackhaulOpportunity(
            vehicleId = vehicle.id,
            outboundWarehouseId = vehicle.currentWarehouse.id,
            returnWarehouseId = destinationWarehouse.id,
            packages = selectedPackages,
            totalCargoWeightKg = totalCargoWeight,
            remainingCapacityKg = vehicle.maxCapacityKg - totalCargoWeight
        )
    }
}