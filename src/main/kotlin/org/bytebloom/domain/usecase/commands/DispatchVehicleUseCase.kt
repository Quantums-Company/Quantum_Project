package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

class DispatchVehicleUseCase {

    operator fun invoke(
        packages: List<Package>,
        vehicle: Vehicle,
        warehouse: Warehouse
    ): Boolean {
        val totalWeight = calculateTotalWeight(packages)

        if (!canDispatch(packages, vehicle, warehouse, totalWeight)) {
            return false
        }

        dispatch(packages, vehicle, warehouse)
        return true

    }

    private fun calculateTotalWeight(packages: List<Package>): Double =
        packages.sumOf { it.weight }

    private fun canDispatch(
        packages: List<Package>,
        vehicle: Vehicle,
        warehouse: Warehouse,
        totalWeight: Double
    ): Boolean =
        packages.all { warehouse.containsPackage(it) } &&
                warehouse.hasVehicle(vehicle) &&
                vehicle.canCarryWeight(totalWeight)


    private fun dispatch(
        packages: List<Package>,
        vehicle: Vehicle,
        warehouse: Warehouse
    ) {
        packages.forEach { warehouse.removePackage(it) }
        warehouse.removeVehicle(vehicle)
    }

}