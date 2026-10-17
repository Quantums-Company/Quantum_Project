package org.bytebloom.logiroute.domain.commandPattern

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.usecase.dispatch.AssignPackageToCargoQueueUseCase

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