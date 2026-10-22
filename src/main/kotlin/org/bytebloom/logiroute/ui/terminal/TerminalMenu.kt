package org.bytebloom.logiroute.ui.terminal

import org.bytebloom.logiroute.ui.screens.AnalyticsTerminalUI
import org.bytebloom.logiroute.ui.screens.ClassificationTerminalUI
import org.bytebloom.logiroute.ui.screens.DispatchTerminalUI
import org.bytebloom.logiroute.ui.screens.FleetPlanningTerminalUI
import org.bytebloom.logiroute.ui.screens.SystemOverviewTerminalUI
import org.bytebloom.logiroute.ui.screens.PackagesManagementTerminalUI
import org.bytebloom.logiroute.ui.screens.PricingTerminalUI
import org.bytebloom.logiroute.ui.screens.RouteAnalysisTerminalUI
import org.bytebloom.logiroute.ui.screens.RoutesManagementTerminalUI
import org.bytebloom.logiroute.ui.screens.ShipmentsTerminalUI
import org.bytebloom.logiroute.ui.screens.ShortestRouteTerminalUI
import org.bytebloom.logiroute.ui.screens.TelemetryTerminalUI
import org.bytebloom.logiroute.ui.screens.VehiclesManagementTerminalUI
import org.bytebloom.logiroute.ui.screens.WarehousesManagementTerminalUI
import org.koin.core.Koin
import org.bytebloom.logiroute.ui.screens.TelemetrySearchTerminalUI

/**
 * Builds the menu of the terminal application.
 *
 * @param showAllScreens `false` shows only the screens required by the assignment (Task 5);
 *                       `true` adds every other screen of the application after them.
 */
fun buildTerminalMenu(koin: Koin, showAllScreens: Boolean): List<MenuSection> {
    val taskFive = MenuSection(
        title = if (showAllScreens) "Task 5" else null,
        entries = listOf(
            MenuEntry("Plan the shortest route", setOf("route", "shortest"),
                koin.get<ShortestRouteTerminalUI>()),
            MenuEntry("Classify a package (KNN)", setOf("classify", "knn"),
                koin.get<ClassificationTerminalUI>()),
            MenuEntry("Live vehicle telemetry", setOf("telemetry", "live"),
                koin.get<TelemetryTerminalUI>()),
            MenuEntry("Debounced telemetry search (bonus)", setOf("search", "debounce"),
                koin.get<TelemetrySearchTerminalUI>())
        )
    )
    if (!showAllScreens) return listOf(taskFive)

    return listOf(
        taskFive,
        MenuSection(
            "Overview",
            listOf(MenuEntry("System overview", setOf("overview"),
                koin.get<SystemOverviewTerminalUI>()))
        ),
        MenuSection(
            "Manage data",
            listOf(
                MenuEntry("Warehouses", setOf("warehouses"),
                    koin.get<WarehousesManagementTerminalUI>()),
                MenuEntry("Vehicles", setOf("vehicles"),
                    koin.get<VehiclesManagementTerminalUI>()),
                MenuEntry("Packages", setOf("packages"),
                    koin.get<PackagesManagementTerminalUI>()),
                MenuEntry("Routes", setOf("routes"),
                    koin.get<RoutesManagementTerminalUI>())
            )
        ),
        MenuSection(
            "Operations",
            listOf(
                MenuEntry("Route analysis", setOf("analysis", "hops"),
                    koin.get<RouteAnalysisTerminalUI>()),
                MenuEntry("Dispatch", setOf("dispatch"),
                    koin.get<DispatchTerminalUI>()),
                MenuEntry("Fleet planning", setOf("fleet", "planning"),
                    koin.get<FleetPlanningTerminalUI>()),
                MenuEntry("Pricing", setOf("pricing", "price"),
                    koin.get<PricingTerminalUI>()),
                MenuEntry("Shipments", setOf("shipments"),
                    koin.get<ShipmentsTerminalUI>()),
                MenuEntry("Analytics (BST vs AVL)", setOf("analytics", "trees"),
                    koin.get<AnalyticsTerminalUI>())
            )
        )
    )
}
