package org.bytebloom.domain.commandPattern

import org.bytebloom.domain.usecase.required.DispatchVehicleUseCase
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

class DispatchVehicleCommand (
    private val dispatchVehicleUseCase: DispatchVehicleUseCase,
    private val pkg: Package,
    private val vehicle: Vehicle,
    private val warehouse: Warehouse
) : Command {

    private var previousState: DispatchState? = null
    private var wasExecuted = false

    override fun execute() {
        val packageIndex = warehouse.cargoQueue.indexOf(pkg)
        val vehicleIndex = warehouse.stationedVehicles.indexOf(vehicle)

        if (packageIndex == -1) throw IllegalStateException("Package not found in warehouse")
        if (vehicleIndex == -1) throw IllegalStateException("Vehicle not stationed here")
        if (!vehicle.available) throw IllegalStateException("Vehicle is not available")
        if (vehicle.cargo.sumOf { it.weight } + pkg.weight > vehicle.maxCapacityKg) {
            throw IllegalStateException("Exceeds vehicle capacity")
        }

        previousState = DispatchState(
            packageIndex = packageIndex,
            vehicleIndex = vehicleIndex,
            previousWarehouse = vehicle.currentWarehouse,
            previousCargo = vehicle.cargo,
            wasAvailable = vehicle.available
        )
        wasExecuted = dispatchVehicleUseCase(pkg, vehicle, warehouse)
    }

    override fun undo() {
        if (!wasExecuted) return
        val state = previousState?: return

        warehouse.restorePackage(pkg, state.packageIndex)
        warehouse.restoreVehicle(vehicle, state.vehicleIndex)

        wasExecuted = false

    }

    private data class DispatchState(
        val packageIndex: Int,
        val vehicleIndex: Int,
        val previousWarehouse: Warehouse?,
        val previousCargo: List<Package>,
        val wasAvailable: Boolean
    )
}
