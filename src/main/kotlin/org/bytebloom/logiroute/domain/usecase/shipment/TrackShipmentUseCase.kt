package org.bytebloom.logiroute.domain.usecase.shipment

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.shipment.Shipment
import org.bytebloom.logiroute.domain.shipment.ShipmentTracker

/** Returns the shipment the system currently holds for a package, creating it when it is new. */
class TrackShipmentUseCase(private val shipmentTracker: ShipmentTracker) {

    operator fun invoke(pkg: Package): Shipment = shipmentTracker.track(pkg)
}
