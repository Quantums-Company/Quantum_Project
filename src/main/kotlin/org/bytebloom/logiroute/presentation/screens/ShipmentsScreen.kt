package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.input.ShipmentTransitionAction
import org.bytebloom.logiroute.domain.shipment.Shipment

interface ShipmentsScreen : BaseScreen {
    fun showShipmentData(packages: List<Package>, warehouses: List<Warehouse>)
    fun showShipment(shipment: Shipment)
    fun showShipmentAdvanced(shipment: Shipment, action: ShipmentTransitionAction)
    fun showShipmentRerouted(shipment: Shipment)
    fun showDeliveryEstimate(packageId: String, totalDelayMinutes: Double?)
}
