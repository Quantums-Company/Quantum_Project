package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.presentation.presenter.RoutesManagementPresenter
import org.bytebloom.logiroute.presentation.screens.RoutesManagementScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.MenuAction
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.askDouble
import org.bytebloom.logiroute.ui.terminal.askInt
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.confirm
import org.bytebloom.logiroute.ui.terminal.runActionMenu
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.routeTable
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

/** The terminal implementation of [RoutesManagementScreen]. */
class RoutesManagementTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (RoutesManagementScreen) -> RoutesManagementPresenter
) : TerminalScreenBase(terminal, style, uiScope), RoutesManagementScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var routes: List<Route> = emptyList()

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    override suspend fun show() {
        heading("Routes (roads between warehouses)")
        loadingMessage = "Loading routes…"
        presenter.onViewReady().join()

        terminal.runActionMenu(
            style,
            "Routes — what do you want to do?",
            listOf(
                MenuAction("List all routes", setOf("list")) { listAll() },
                MenuAction("Find one by id", setOf("find")) { find() },
                MenuAction("Create a route", setOf("create", "new")) { create() },
                MenuAction("Update a route", setOf("update", "edit")) { update() },
                MenuAction("Delete a route", setOf("delete")) { delete() }
            )
        )
    }

    private suspend fun listAll() {
        loadingMessage = "Loading routes…"
        presenter.onViewReady().join()
    }

    private suspend fun find() {
        val chosen = pickRoute("Route to show") ?: return
        loadingMessage = "Looking for ${chosen.id}…"
        presenter.onFindById(chosen.id).join()
    }

    private suspend fun create() {
        val origin = pickWarehouse("From warehouse") ?: return
        val destination = pickWarehouse("To warehouse") ?: return
        val distance = terminal.askDouble("Distance in km (Enter to cancel): ", style) ?: return
        val delay = terminal.askInt("Typical delay in minutes (Enter to cancel): ", style) ?: return

        loadingMessage = "Creating the route…"
        presenter.onCreate(
            distance,
            delay,
            origin.id,
            destination.id
        ).join()
    }

    private suspend fun update() {
        val chosen = pickRoute("Route to update") ?: return
        terminal.writeLine(style.dim("  Press Enter to keep the current value."))
        val distance = terminal.askDouble("Distance in km [${chosen.distanceKm}]: ", style)
        val delay = terminal.askInt("Typical delay in minutes [${chosen.typicalDelayMin}]: ", style)
        val origin = if (terminal.confirm("Change the origin" +
                    " (now ${chosen.originWarehouse.name})?")) pickWarehouse("New origin") else null
        val destination = if (terminal.confirm("Change the destination " +
                    "(now ${chosen.destinationWarehouse.name})?")) {
            pickWarehouse("New destination")
        } else {
            null
        }

        loadingMessage = "Updating ${chosen.id}…"
        presenter.onUpdate(
            chosen.id,
            distance,
            delay,
            origin?.id,
            destination?.id
        ).join()
    }

    private suspend fun delete() {
        val chosen = pickRoute("Route to delete") ?: return
        if (!terminal.confirm("Delete ${chosen.id}" +
                    " (${chosen.originWarehouse.name} → ${chosen.destinationWarehouse.name})?")) return

        loadingMessage = "Deleting ${chosen.id}…"
        presenter.onDelete(chosen.id).join()
    }

    private suspend fun pickRoute(label: String): Route? {
        if (routes.isEmpty()) {
            printWarning("There are no routes yet.")
            return null
        }
        printLines(style.routeTable(routes, numbered = true))
        return terminal.chooseFrom("$label (index or id, Enter to cancel): ",
            routes, Route::id, style)
    }

    private suspend fun pickWarehouse(label: String): Warehouse? {
        if (warehouses.isEmpty()) {
            printWarning("There are no warehouses yet.")
            return null
        }
        printLines(style.warehouseTable(warehouses, numbered = true))
        return terminal.chooseFrom("$label (index or id, Enter to cancel): ",
            warehouses, Warehouse::id, style)
    }

    override fun showRoutes(routes: List<Route>) {
        this.routes = routes
        terminal.writeLine()
        if (routes.isEmpty())
            printWarning("There are no routes yet.")
        else
            printLines(style.routeTable(routes))
    }

    override fun showWarehouseChoices(warehouses: List<Warehouse>) {
        this.warehouses = warehouses
    }

    override fun showRoute(route: Route) {
        printLines(
            style.box(
                title = route.id,
                lines = listOf(
                    "From       ${route.originWarehouse.name} (${route.originWarehouse.id})",
                    "To         ${route.destinationWarehouse.name} (${route.destinationWarehouse.id})",
                    "Distance   ${formatNumber(route.distanceKm)} km",
                    "Delay      ${route.typicalDelayMin} min"
                )
            )
        )
    }

    override fun showRouteNotFound(id: String) = printWarning("Route '$id' was not found.")

    override fun showRouteCreated(route: Route) {
        routes = routes + route
        printSuccess("Created ${route.id}: ${route.originWarehouse.name}" +
                " ${style.glyphs.arrow} ${route.destinationWarehouse.name}.")
    }

    override fun showRouteUpdated(route: Route) {
        routes = routes.map { if (it.id == route.id) route else it }
        printSuccess("Updated ${route.id}.")
    }

    override fun showRouteDeleted(id: String, wasDeleted: Boolean) {
        if (wasDeleted) {
            routes = routes.filterNot { it.id == id }
            printSuccess("Deleted $id.")
        } else {
            printWarning("Nothing was deleted: $id does not exist.")
        }
    }
}

