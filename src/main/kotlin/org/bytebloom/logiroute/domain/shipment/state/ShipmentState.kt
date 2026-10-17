package org.bytebloom.logiroute.domain.shipment.state

import org.bytebloom.logiroute.domain.model.exception.IllegalShipmentTransitionException

sealed interface ShipmentState {
    val name: String

    fun assignToVehicle(): ShipmentState =
        illegalTransition("assign to vehicle")

    fun startTransit(): ShipmentState =
        illegalTransition("start transit")

    fun markDelivered(): ShipmentState =
        illegalTransition("mark delivered")

    fun markDeliveryFailed(): ShipmentState =
        illegalTransition("mark delivery failed")

    private fun illegalTransition(action: String): Nothing =
        throw IllegalShipmentTransitionException(
            currentState = name,
            attemptedAction = action
        )
}

