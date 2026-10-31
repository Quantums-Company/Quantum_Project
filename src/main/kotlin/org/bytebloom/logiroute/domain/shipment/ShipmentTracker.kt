package org.bytebloom.logiroute.domain.shipment

import org.bytebloom.logiroute.domain.dispatch.DispatchOrder
import org.bytebloom.logiroute.domain.dispatch.ShipmentStateUpdater
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException
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
            validateNoDuplicates(order.packages)

            val pendingUpdates = prepareDispatchUpdates(order.packages)

            pendingUpdates.forEach { (packageId, initialShipment, transitions) ->
                registerInitialShipmentIfNeeded(packageId, initialShipment)
                transitions.forEach(::save)
            }
        }
    }

    private fun validateNoDuplicates(packages: List<Package>) {
        val duplicateIds = packages
            .groupingBy { it.id }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        if (duplicateIds.isNotEmpty()) {
            throw InvalidConfigurationException(
                "A dispatch order cannot contain duplicate package IDs."
            )
        }
    }

    private data class DispatchUpdate(
        val packageId: String,
        val initialShipment: Shipment,
        val transitions: List<Shipment>
    )

    private fun prepareDispatchUpdates(packages: List<Package>): List<DispatchUpdate> {
        return packages.map { pkg ->
            val currentShipment = shipmentsByPackageId[pkg.id] ?: Shipment(pkg)
            val assignedShipment = currentShipment.assignToVehicle()
            val inTransitShipment = assignedShipment.startTransit()

            DispatchUpdate(
                packageId = pkg.id,
                initialShipment = currentShipment,
                transitions = listOf(assignedShipment, inTransitShipment)
            )
        }
    }

    private fun registerInitialShipmentIfNeeded(packageId: String, initialShipment: Shipment) {
        if (packageId !in shipmentsByPackageId) {
            shipmentsByPackageId[packageId] = initialShipment
            recordState(initialShipment)
        }
    }

    private fun save(shipment: Shipment) {
        shipmentsByPackageId[shipment.cargo.id] = shipment
        recordState(shipment)
    }

    private fun recordState(shipment: Shipment) {
        historyByPackageId.getOrPut(shipment.cargo.id) { mutableListOf() }.add(shipment.state)
    }
}