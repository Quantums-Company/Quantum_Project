package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.result.BackhaulOpportunity
import org.bytebloom.logiroute.domain.model.result.CargoRecoveryPlan
import org.bytebloom.logiroute.domain.model.result.DispatchResult

interface FleetPlanningScreen : BaseScreen {
    fun showFleetData(warehouses: List<Warehouse>, vehicles: List<Vehicle>)
    fun showCoveragePlan(targetZones: Set<String>, result: DispatchResult)
    fun showBackhaulOpportunity(vehicle: Vehicle, returnFrom: Warehouse, opportunity: BackhaulOpportunity?)
    fun showRecoveryPlan(failedVehicle: Vehicle, plan: CargoRecoveryPlan?)
}
