package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.dispatch.DispatchMode
import org.bytebloom.logiroute.domain.dispatch.DispatchOutcome
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.result.KnapsackResult

/** What the dispatch screen must be able to show. Implemented by the UI. */
interface DispatchScreen : BaseScreen {

    /** The current warehouses, vehicles and packages, to offer them in pickers. Nothing is displayed. */
    fun showDispatchData(warehouses: List<Warehouse>, vehicles: List<Vehicle>, packages: List<Package>)

    /** [wasAssigned] is false when the package does not start at that warehouse or is already queued there. */
    fun showPackageAssigned(pkg: Package, warehouse: Warehouse, wasAssigned: Boolean)

    fun showVehicleStationed(vehicle: Vehicle)

    fun showVehicleDispatched(wasDispatched: Boolean, vehicle: Vehicle, packages: List<Package>)

    fun showDispatchOutcome(mode: DispatchMode, outcome: DispatchOutcome)

    fun showLoadPlan(vehicle: Vehicle, candidates: List<Package>, result: KnapsackResult)
}
