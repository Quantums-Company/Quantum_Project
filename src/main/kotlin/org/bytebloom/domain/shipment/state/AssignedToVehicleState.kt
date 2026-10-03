package org.bytebloom.domain.shipment.state

class AssignedToVehicleState : ShipmentState {
    override val name = "ASSIGNED_TO_VEHICLE"
    override fun startTransit(): ShipmentState =
        InTransitState()
}