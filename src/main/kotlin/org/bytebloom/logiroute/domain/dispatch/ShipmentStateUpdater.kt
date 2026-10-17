package org.bytebloom.logiroute.domain.dispatch

fun interface ShipmentStateUpdater {
    fun markAsDispatched(order: DispatchOrder)
}