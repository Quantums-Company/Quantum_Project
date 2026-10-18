package org.bytebloom.logiroute.domain.usecase.shipment

import org.bytebloom.logiroute.domain.model.input.ShipmentTransitionAction
import org.bytebloom.logiroute.domain.shipment.Shipment

class AdvanceShipmentStateUseCase {

    operator fun invoke(
        shipment: Shipment,
        action: ShipmentTransitionAction
    ): Shipment =
        when (action) {
            ShipmentTransitionAction.ASSIGN_TO_VEHICLE ->
                shipment.assignToVehicle()

            ShipmentTransitionAction.START_TRANSIT ->
                shipment.startTransit()

            ShipmentTransitionAction.MARK_DELIVERED ->
                shipment.markDelivered()

            ShipmentTransitionAction.MARK_DELIVERY_FAILED ->
                shipment.markDeliveryFailed()
        }
}