package org.bytebloom.logiroute.domain.usecase.shipment

import org.bytebloom.logiroute.domain.shipment.Shipment
import org.bytebloom.logiroute.domain.shipment.ShipmentTracker

/** Stores a shipment after it was advanced or rerouted, so dispatch and tracking agree on its state. */
class SaveShipmentUseCase(private val shipmentTracker: ShipmentTracker) {

    operator fun invoke(shipment: Shipment): Shipment = shipmentTracker.update(shipment)
}
