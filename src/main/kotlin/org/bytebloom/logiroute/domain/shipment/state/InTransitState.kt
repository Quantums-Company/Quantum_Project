package org.bytebloom.logiroute.domain.shipment.state

class InTransitState : ShipmentState {
    override val name = "IN_TRANSIT"
    override fun markDelivered(): ShipmentState =
        DeliveredState()

    override fun markDeliveryFailed(): ShipmentState =
        DeliveryFailedState()
}