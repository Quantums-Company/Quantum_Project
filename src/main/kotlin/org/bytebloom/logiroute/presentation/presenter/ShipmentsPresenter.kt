package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.ShipmentTransitionAction
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.shipment.Shipment
import org.bytebloom.logiroute.domain.usecase.dispatch.ReroutePackageUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.shipment.AdvanceShipmentStateUseCase
import org.bytebloom.logiroute.domain.usecase.shipment.EstimateShipmentDeliveryUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.RoutingDataLoader
import org.bytebloom.logiroute.presentation.screens.ShipmentsScreen

/**
 * Follows the life of shipments. The presenter keeps the shipments it has opened (by package id),
 * so a later action continues from the state the user already reached.
 */
class ShipmentsPresenter(
    private val getAllPackages: GetAllPackagesUseCase,
    getAllWarehouses: GetAllWarehousesUseCase,
    getAllRoutes: GetAllRoutesUseCase,
    private val advanceShipmentState: AdvanceShipmentStateUseCase,
    private val reroutePackage: ReroutePackageUseCase,
    private val estimateFor: (WarehouseGraph) -> EstimateShipmentDeliveryUseCase,
    view: ShipmentsScreen,
    parentScope: CoroutineScope
) : BasePresenter<ShipmentsScreen>(view, parentScope) {

    private class Data(val packages: List<Package>, val warehouses: List<Warehouse>)

    private val loader = RoutingDataLoader(getAllWarehouses, getAllRoutes)
    private val shipments = mutableMapOf<String, Shipment>()

    @Volatile
    private var data: Data? = null

    fun onViewReady(): Job = perform(
        task = {
            coroutineScope {
                val packages = async { getAllPackages() }
                val routing = async { loader.load() }
                Data(packages.await(), routing.await().warehouses)
            }
        }
    ) {
        data = it
        view.showShipmentData(it.packages, it.warehouses)
    }

    fun onTrack(packageId: String): Job = perform(task = { shipmentOf(packageId) }) { view.showShipment(it) }

    fun onAdvance(packageId: String, action: ShipmentTransitionAction): Job = perform(
        task = {
            val advanced = advanceShipmentState(shipmentOf(packageId), action)
            synchronized(shipments) { shipments[advanced.cargo.id] = advanced }
            advanced
        }
    ) { view.showShipmentAdvanced(it, action) }

    fun onReroute(packageId: String, newDestinationId: String): Job = perform(
        task = {
            val known = loaded()
            val destination = known.warehouses
                .firstOrNull { it.id.equals(newDestinationId.trim(), ignoreCase = true) }
                ?: throw ResourceNotFoundException("Warehouse '$newDestinationId' was not found.")
            val rerouted = reroutePackage(shipmentOf(packageId), destination)
            synchronized(shipments) { shipments[rerouted.cargo.id] = rerouted }
            rerouted
        }
    ) { view.showShipmentRerouted(it) }

    fun onEstimateDelivery(packageId: String): Job = perform(
        task = {
            val pkg = shipmentOf(packageId).cargo
            val graph = loader.load().graph
            pkg.id to estimateFor(graph)(pkg.id)
        }
    ) { (id, minutes) -> view.showDeliveryEstimate(id, minutes) }

    private fun shipmentOf(packageId: String): Shipment {
        val pkg = loaded().packages.firstOrNull { it.id.equals(packageId.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Package '$packageId' was not found.")
        return synchronized(shipments) { shipments.getOrPut(pkg.id) { Shipment(pkg) } }
    }

    private fun loaded(): Data = data ?: throw IllegalStateException("The shipment data is not loaded yet.")
}
