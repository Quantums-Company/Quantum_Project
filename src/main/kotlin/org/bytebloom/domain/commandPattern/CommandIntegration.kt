package org.bytebloom.domain.commandPattern

import org.bytebloom.domain.model.*
import org.bytebloom.domain.usecase.required.*

class CommandIntegration(
    val commandInvoker: CommandInvoker,
    val assignPackageToCargoQueueUseCase: AssignPackageToCargoQueueUseCase ,
    val dispatchVehicleUseCase: DispatchVehicleUseCase
) {
    fun createAssignCommand(warehouse: Warehouse, pkg: Package):AssignPackageToQueueCommand {

        requireNotNull(warehouse.id){"Warehouse ID is require"}
        require(warehouse.cargoQueue.contains(pkg).not()) { "Package already in queue" }

        return AssignPackageToQueueCommand(warehouse = warehouse, packageData = pkg, assignPackageToQueue = assignPackageToCargoQueueUseCase)}

    fun createDispatchCommand(pkg: Package, vehicle: Vehicle, warehouse: Warehouse):DispatchVehicleCommand {
        return DispatchVehicleCommand(dispatchVehicleUseCase = dispatchVehicleUseCase, pkg = pkg, vehicle = vehicle, warehouse = warehouse)}

    fun execute(command: Command): Boolean = commandInvoker.execute(command)
    fun undo(): Boolean = commandInvoker.undo()
    fun canUndo(): Boolean = commandInvoker.canUndo()
    fun clearHistory() = commandInvoker.clearHistory()

}