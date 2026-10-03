package org.bytebloom.domain.dispatch

fun interface ShipmentStateUpdater {
    fun markAsDispatched(order: DispatchOrder)
}