package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.presentation.dependencies.FleetPlanningDependencies
import org.bytebloom.logiroute.presentation.screens.FleetPlanningScreen
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField

class FleetPlanningPresenter(
    private val dependencies: FleetPlanningDependencies,
    view: FleetPlanningScreen,
    parentScope: CoroutineScope
) : BasePresenter<FleetPlanningScreen>(view, parentScope) {

    private class Data(val warehouses: List<Warehouse>, val vehicles: List<Vehicle>)

    @Volatile
    private var data: Data? = null

    fun onViewReady(): Job = perform(task = ::load) {
        data = it
        view.showFleetData(it.warehouses, it.vehicles)
    }

    /** Which vehicles cover all [targetZones]? A vehicle covers its own zone and where its warehouse has routes. */
    fun onPlanCoverage(targetZones: Set<String>): Job = perform(
        task = {
            val known = loaded()
            val wanted = targetZones.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            if (wanted.isEmpty()) {
                throw EntityValidationException(
                    listOf(
                        ValidatorError.Custom(
                            ValidatorField.REGIONAL_ZONE,
                            "Choose at least one target zone."
                        )
                    )
                )
            }
            withContext(dependencies.computeDispatcher) {
                dependencies.greedyFleetDispatch(wanted, known.vehicles, ::coverageOf)
            }
        }
    ) { view.showCoveragePlan(targetZones, it) }

    fun onFindBackhaul(vehicleId: String, returnFromWarehouseId: String): Job = perform(
        task = {
            val known = loaded()
            val vehicle = known.vehicle(vehicleId)
            val warehouse = known.warehouse(returnFromWarehouseId)
            Triple(vehicle, warehouse, dependencies.findBackhaulOpportunity(vehicle, warehouse))
        }
    ) { (vehicle, warehouse, opportunity) -> view.showBackhaulOpportunity(
        vehicle,
        warehouse,
        opportunity
    ) }

    fun onFindRecovery(failedVehicleId: String): Job = perform(
        task = {
            val vehicle = loaded().vehicle(failedVehicleId)
            vehicle to dependencies.findCargoRecoveryPlan(vehicle)
        }
    ) { (vehicle, plan) -> view.showRecoveryPlan(vehicle, plan) }

    private fun coverageOf(vehicle: Vehicle): Set<String> =
        buildSet {
            add(vehicle.currentWarehouse.regionalZone)
            vehicle.currentWarehouse.outgoingRoutes.forEach { add(it.destinationWarehouse.regionalZone) }
        }

    private suspend fun load(): Data = coroutineScope {
        val warehouses = async { dependencies.getAllWarehouses() }
        val vehicles = async { dependencies.getAllVehicles() }
        Data(warehouses.await(), vehicles.await())
    }

    private fun loaded(): Data =
        data ?: throw InvalidConfigurationException(
            "Fleet data has not been loaded. Call onViewReady() before performing this action."
        )
    private fun Data.vehicle(id: String): Vehicle =
        vehicles.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Vehicle '$id' was not found.")

    private fun Data.warehouse(id: String): Warehouse =
        warehouses.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")
}