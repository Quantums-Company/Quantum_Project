package org.bytebloom.logiroute.domain.usecase.commands

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.IllegalShipmentTransitionException
import org.bytebloom.logiroute.domain.shipment.Shipment
import org.bytebloom.logiroute.domain.shipment.state.AssignedToVehicleState
import org.bytebloom.logiroute.domain.shipment.state.CreatedState

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