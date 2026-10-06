package org.bytebloom.domain.model.input

enum class ShipmentTransitionAction {
    ASSIGN_TO_VEHICLE,
    START_TRANSIT,
    MARK_DELIVERED,
    MARK_DELIVERY_FAILED
}