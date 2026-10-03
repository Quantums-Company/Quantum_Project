package org.bytebloom.domain.shipment

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.shipment.state.CreatedState
import org.bytebloom.domain.shipment.state.ShipmentState

data class Shipment(
    val cargo: Package,
    val state: ShipmentState = CreatedState()
) {

    fun assignToVehicle(): Shipment =
        copy(state = state.assignToVehicle())

    fun startTransit(): Shipment =
        copy(state = state.startTransit())

    fun markDelivered(): Shipment =
        copy(state = state.markDelivered())

    fun markDeliveryFailed(): Shipment =
        copy(state = state.markDeliveryFailed())
}