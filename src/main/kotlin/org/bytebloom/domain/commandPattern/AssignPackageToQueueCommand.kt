package org.bytebloom.domain.commandPattern

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.AssignPackageToCargoQueueUseCase

class AssignPackageToQueueCommand(
    private val warehouse: Warehouse,
    private val packageItem: Package,
    private val assignPackageToQueue: AssignPackageToCargoQueueUseCase
) : Command {

    override fun execute(): Boolean =
        assignPackageToQueue(
            warehouse,
            packageItem
        )

    override fun undo(): Boolean =
         warehouse.removePackage(packageItem)
}