package org.bytebloom.logiroute.domain.shipment

import org.bytebloom.logiroute.domain.dispatch.DispatchOrder
import org.bytebloom.logiroute.domain.dispatch.ShipmentStateUpdater
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.shipment.state.ShipmentState

class ShipmentTracker : ShipmentStateUpdater {

    private val lock = Any()
    private val shipmentsByPackageId = mutableMapOf<String, Shipment>()
    private val historyByPackageId = mutableMapOf<String, MutableList<ShipmentState>>()

    fun track(pkg: Package): Shipment = synchronized(lock) {
        shipmentsByPackageId.getOrPut(pkg.id) {
            Shipment(pkg).also(::recordState)
        }
    }

    fun stateOf(packageId: String): ShipmentState? = synchronized(lock) {
        shipmentsByPackageId[packageId]?.state
    }

    fun historyOf(packageId: String): List<ShipmentState> = synchronized(lock) {
        historyByPackageId[packageId]?.toList().orEmpty()
    }

    override fun markAsDispatched(order: DispatchOrder) {
        synchronized(lock) {
            order.packages.forEach(::dispatch)
        }
    }

    private fun dispatch(pkg: Package) {
        val assignedShipment = track(pkg).assignToVehicle()
        save(assignedShipment)
        save(assignedShipment.startTransit())
    }

    private fun save(shipment: Shipment) {
        shipmentsByPackageId[shipment.cargo.id] = shipment
        recordState(shipment)
    }

    private fun recordState(shipment: Shipment) {
        historyByPackageId.getOrPut(shipment.cargo.id) { mutableListOf() }.add(shipment.state)
    }
}
