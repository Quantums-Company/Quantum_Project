package org.bytebloom.domain.model.exception

class IllegalShipmentTransitionException(
    val currentState: String,
    val attemptedAction: String
) : DomainException("Cannot $attemptedAction while shipment is in state '$currentState'")