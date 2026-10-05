package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.IllegalShipmentTransitionException
import org.bytebloom.domain.shipment.Shipment
import org.bytebloom.domain.shipment.state.AssignedToVehicleState
import org.bytebloom.domain.shipment.state.CreatedState

class ReroutePackageUseCase {
    operator fun invoke(shipment: Shipment, newDestination: Warehouse): Shipment {
        if (shipment.state !is CreatedState && shipment.state !is AssignedToVehicleState) {
            throw IllegalShipmentTransitionException(
                currentState = shipment.state.name,
                attemptedAction = "reroute to a new destination"
            )
        }
        return shipment.copy(cargo = shipment.cargo.redirectedTo(newDestination))
    }
}