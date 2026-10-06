package org.bytebloom.domain.dispatch

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle

class StandardDispatchProcessor(
    shipmentStateUpdater: ShipmentStateUpdater
) : BaseDispatchProcessor(shipmentStateUpdater) {

    override fun isPriorityAllowed(pkg: Package): Boolean = true

    override fun maxLoadKg(vehicle: Vehicle): Double = vehicle.maxCapacityKg
}