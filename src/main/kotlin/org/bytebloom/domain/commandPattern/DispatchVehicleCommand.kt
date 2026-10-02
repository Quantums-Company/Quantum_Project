package org.bytebloom.domain.commandPattern

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.DispatchVehicleUseCase

class DispatchVehicleCommand(
    private val dispatchVehicleUseCase: DispatchVehicleUseCase,
    private val packages: List<Package>,
    private val vehicle: Vehicle,
    private val warehouse: Warehouse
) : Command {

    override fun execute(): Boolean = dispatchVehicleUseCase(packages, vehicle, warehouse)

    override fun undo(): Boolean {
        packages.forEach { warehouse.addPackage(it) }
        warehouse.addVehicle(vehicle)
        return true
    }
}