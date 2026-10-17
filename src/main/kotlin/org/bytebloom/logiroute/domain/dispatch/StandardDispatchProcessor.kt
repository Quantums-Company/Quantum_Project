package org.bytebloom.logiroute.domain.dispatch

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle

class StandardDispatchProcessor(
    shipmentStateUpdater: ShipmentStateUpdater
) : BaseDispatchProcessor(shipmentStateUpdater) {

    override fun isPriorityAllowed(pkg: Package): Boolean = true

    override fun maxLoadKg(vehicle: Vehicle): Double = vehicle.maxCapacityKg
}