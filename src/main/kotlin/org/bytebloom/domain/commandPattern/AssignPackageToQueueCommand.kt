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

    override fun execute(): Boolean =
        assignPackageToQueue(
            warehouse,
            packageItem
        ).also { success ->
            if (!success) {
                Logger.error(
                    "Failed to assign package '${packageItem.id}' " +
                            "to warehouse '${warehouse.id}'."
                )
            }
        }

    override fun undo(): Boolean {

        val removed =
            warehouse.removePackage(packageItem)

        if (!removed) {
            Logger.error(
                "Cannot undo package assignment: " +
                        "package '${packageItem.id}' " +
                        "is not in warehouse '${warehouse.id}'."
            )
        }

        return removed
    }
}