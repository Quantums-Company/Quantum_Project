package org.bytebloom.domain.shipment.state

class CreatedState : ShipmentState {
    override val name = "CREATED"
    override fun assignToVehicle(): ShipmentState =
        AssignedToVehicleState()
}