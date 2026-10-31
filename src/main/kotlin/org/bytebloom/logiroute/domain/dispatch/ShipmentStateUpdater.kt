package org.bytebloom.logiroute.domain.dispatch

interface ShipmentStateUpdater {
    fun markAsDispatched(order: DispatchOrder)
}