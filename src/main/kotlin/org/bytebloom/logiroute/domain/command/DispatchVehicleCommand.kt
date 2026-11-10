package org.bytebloom.logiroute.domain.command

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchVehicleUseCase

class DispatchVehicleCommand(
    private val dispatchVehicleUseCase: DispatchVehicleUseCase,
    private val packages: List<Package>,
    private val vehicle: Vehicle,
    private val warehouse: Warehouse
) : Command {

    override fun execute(): Boolean =
        dispatchVehicleUseCase(packages, vehicle, warehouse)

    override fun undo(): Boolean {
        if (
            packages.any(warehouse::containsPackage) ||
            warehouse.hasVehicle(vehicle)
        ) {
            return false
        }

        if (!restorePackages()) return false

        if (warehouse.addVehicle(vehicle)) return true

        packages.forEach(warehouse::removePackage)
        return false
    }

    private fun restorePackages(): Boolean {
        val restoredPackages = mutableListOf<Package>()

        for (pkg in packages) {
            if (!warehouse.addPackage(pkg)) {
                restoredPackages.forEach(warehouse::removePackage)
                return false
            }

            restoredPackages.add(pkg)
        }

        return true
    }
}