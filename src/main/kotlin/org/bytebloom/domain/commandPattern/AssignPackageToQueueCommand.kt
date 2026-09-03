package org.bytebloom.domain.commandPattern

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.AssignPackageToCargoQueueUseCase
import org.bytebloom.util.Logger

class AssignPackageToQueueCommand(
    private val warehouse: Warehouse,
    private val packageItem: Package,
    private val assignPackageToQueue: AssignPackageToCargoQueueUseCase
) : Command {

    override fun execute(): Boolean {
        if (!assignPackageToQueue(warehouse, packageItem)){
            Logger.error("Failed to assign package '${packageItem.id}' " +
                        "to warehouse '${warehouse.id}'.")
            return false

        }
        return true
    }

    override fun undo(): Boolean {
        if (!warehouse.removePackage(packageItem)) {
            Logger.error(
                "Cannot undo package assignment: " +
                        "package '${packageItem.id}' is not in the cargo queue."
            )
            return false
        }
        return true
    }
}