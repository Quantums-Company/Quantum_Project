package org.bytebloom.domain.commandPattern

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.DispatchVehicleUseCase
import org.bytebloom.util.Logger

class DispatchVehicleCommand(
    private val dispatchVehicleUseCase: DispatchVehicleUseCase,
    private val packages: List<Package>,
    private val vehicle: Vehicle,
    private val warehouse: Warehouse
) : Command {

    override fun execute(): Boolean {
        if(!dispatchVehicleUseCase(packages, vehicle, warehouse)) {
            return false
        }
        return true
    }

    override fun undo():Boolean {
        packages.forEach { packageItem ->
            warehouse.addPackage(packageItem)
        }

        warehouse.addVehicle(vehicle)

        Logger.info(
            "Dispatch undone: vehicle '${vehicle.id}' and " +
                    "${packages.size} package(s) restored to " +
                    "warehouse '${warehouse.id}'."
        )
        return true
    }
}