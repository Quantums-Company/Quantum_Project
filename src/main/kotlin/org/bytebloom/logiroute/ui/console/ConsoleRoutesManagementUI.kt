package org.bytebloom.logiroute.ui.console

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.presentation.screen.RoutesManagementScreen

class ConsoleRoutesManagementUI : RoutesManagementScreen {
    override fun showLoadingIndicator(isLoading: Boolean) {
        if (isLoading) println("Preparing route network...")
    }

    override fun showErrorMessage(errorMessage: String) = println("[ERROR] $errorMessage")

    override fun showWarehouses(warehouses: List<Warehouse>) {
        printSection("ROUTE PLANNER — AVAILABLE WAREHOUSES")
        warehouses.forEach { println("${it.id} | ${it.name} | zone=${it.regionalZone}") }
    }

    override fun showRoute(origin: Warehouse, destination: Warehouse, path: List<Warehouse>?, totalDistanceKm: Double) {
        printSection("OPTIMAL ROUTE RESULT")
        println("From: ${origin.id} — ${origin.name}")
        println("To  : ${destination.id} — ${destination.name}")
        if (path == null) {
            println("No route connects these warehouses.")
        } else {
            println(path.joinToString("  →  ") { "${it.id} (${it.name})" })
            println("Stops: ${path.size} | Total distance: %.2f km".format(totalDistanceKm))
        }
    }

    override fun showSkippedRoutes(routeIds: List<String>) {
        if (routeIds.isNotEmpty()) println("Warning: invalid routes skipped: ${routeIds.joinToString()}")
    }
}