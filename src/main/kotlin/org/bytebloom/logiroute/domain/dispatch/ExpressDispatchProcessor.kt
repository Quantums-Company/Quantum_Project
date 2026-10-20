package org.bytebloom.logiroute.domain.dispatch

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Vehicle

class ExpressDispatchProcessor(
    shipmentStateUpdater: ShipmentStateUpdater,
    private val dispatchNotifier: DispatchNotifier
) : BaseDispatchProcessor(shipmentStateUpdater) {

    override fun isPriorityAllowed(pkg: Package): Boolean = pkg.priority == Priority.URGENT

    override fun maxLoadKg(vehicle: Vehicle): Double = vehicle.maxCapacityKg * EXPRESS_LOAD_FACTOR

    override fun notifyDispatchStatus(outcome: DispatchOutcome) {
        dispatchNotifier.notify(outcome)
    }

    private companion object {
        const val EXPRESS_LOAD_FACTOR = 0.8
    }
}