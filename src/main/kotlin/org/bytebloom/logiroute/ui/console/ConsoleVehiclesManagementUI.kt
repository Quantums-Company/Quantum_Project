package org.bytebloom.logiroute.ui.console

import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.presentation.screen.VehiclesManagementScreen

class ConsoleVehiclesManagementUI : VehiclesManagementScreen {
    override fun showLoadingIndicator(isLoading: Boolean) {
        if (isLoading) println("Loading vehicles...")
    }

    override fun showErrorMessage(errorMessage: String) = println("[ERROR] $errorMessage")

    override fun showVehicles(vehicles: List<Vehicle>) {
        printSection("VEHICLE MANAGEMENT")
        if (vehicles.isEmpty()) println("No vehicles found.")
        println("%-14s %-16s %-14s %-18s".format("VEHICLE ID", "CAPACITY (kg)", "COST / km", "CURRENT HUB"))
        println("-".repeat(66))
        vehicles.forEach { vehicle ->
            println("%-14s %-16.2f %-14.2f %-18s".format(
                vehicle.id, vehicle.maxCapacityKg, vehicle.costPerKm, vehicle.currentWarehouse.id
            ))
        }
    }
}