package org.bytebloom.domain.commandPattern

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.required.AssignPackageToCargoQueueUseCase

class AssignPackageToQueueCommand(
    private val warehouse: Warehouse,
    private val packageData: Package,
    private val assignPackageToQueue: AssignPackageToCargoQueueUseCase
) : Command {

    private var executed = false

    override fun execute() {
        if (executed) return

        assignPackageToQueue(warehouse, packageData)
        executed = true
    }

    override fun undo() {
        if (!executed) return

        warehouse.removePackage(packageData)
        executed = false
    }
}