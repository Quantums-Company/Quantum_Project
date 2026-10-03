package org.bytebloom.domain.shipment

import org.bytebloom.domain.dispatch.DispatchOrder
import org.bytebloom.domain.dispatch.ShipmentStateUpdater
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.shipment.state.ShipmentState

class ShipmentTracker : ShipmentStateUpdater {

    private val shipmentsByPackageId = mutableMapOf<String, Shipment>()
    private val historyByPackageId = mutableMapOf<String, MutableList<ShipmentState>>()

    fun track(pkg: Package): Shipment =
        shipmentsByPackageId.getOrPut(pkg.id) {
            Shipment(pkg).also(::recordState)
        }

    fun stateOf(packageId: String): ShipmentState? =
        shipmentsByPackageId[packageId]?.state

    fun historyOf(packageId: String): List<ShipmentState> =
        historyByPackageId[packageId].orEmpty()

    override fun markAsDispatched(order: DispatchOrder) {
        order.packages.forEach(::dispatch)
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