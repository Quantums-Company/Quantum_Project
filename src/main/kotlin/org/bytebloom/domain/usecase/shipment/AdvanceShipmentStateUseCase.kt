package org.bytebloom.domain.usecase.shipment

import org.bytebloom.domain.model.input.ShipmentTransitionAction
import org.bytebloom.domain.shipment.Shipment

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