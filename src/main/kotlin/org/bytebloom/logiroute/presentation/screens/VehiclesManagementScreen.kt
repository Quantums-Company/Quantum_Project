package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse

/** What the vehicle management screen must be able to show. Implemented by the UI. */
interface VehiclesManagementScreen : BaseScreen {

    fun showVehicles(vehicles: List<Vehicle>)

    /** The warehouses a vehicle can be stationed at, to offer them in a picker. */
    fun showWarehouseChoices(warehouses: List<Warehouse>)

    /** The packages that can be carried, to offer them in a picker. */
    fun showPackageChoices(packages: List<Package>)

    fun showVehicle(vehicle: Vehicle)

    fun showVehicleNotFound(id: String)

    fun showVehicleCreated(vehicle: Vehicle)

    fun showVehicleUpdated(vehicle: Vehicle)

    fun showVehicleDeleted(id: String, wasDeleted: Boolean)

    fun showStationedVehicles(warehouse: Warehouse, requiredCapacityKg: Double, vehicles: List<Vehicle>)

    /** [vehicle] is null when no vehicle can carry all [packages] together. */
    fun showCheapestVehicle(packages: List<Package>, vehicle: Vehicle?)
}
