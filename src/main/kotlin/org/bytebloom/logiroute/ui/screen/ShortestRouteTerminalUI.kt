package org.bytebloom.logiroute.ui.screen

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.presentation.screen.ShortestRouteScreen
import org.bytebloom.logiroute.presentation.RoutePlan
import org.bytebloom.logiroute.presentation.presenter.ShortestRoutePresenter
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.hint
import org.bytebloom.logiroute.ui.terminal.widget.table
import org.bytebloom.logiroute.ui.terminal.writeLines

/** The terminal implementation of [ShortestRouteScreen]. An Android UI would implement the same interface. */
class ShortestRouteTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (ShortestRouteScreen) -> ShortestRoutePresenter
) : TerminalScreenBase(terminal, style, uiScope), ShortestRouteScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    override suspend fun show() {
        terminal.writeLine()
        terminal.writeLine(style.bold("Plan the shortest route"))

        warehouses = emptyList()
        loadingMessage = "Loading warehouses and routes…"
        presenter.onViewReady().join()
        if (warehouses.isEmpty()) return

        val origin = terminal.chooseFrom(FROM_PROMPT, warehouses, Warehouse::id, style) ?: return
        val destination = terminal.chooseFrom(TO_PROMPT, warehouses, Warehouse::id, style) ?: return

        loadingMessage = "Calculating the shortest route…"
        presenter.onCalculate(origin.id, destination.id).join()
    }

    override fun showWarehouses(warehouses: List<Warehouse>) {
        this.warehouses = warehouses
        if (warehouses.isEmpty()) {
            terminal.writeLine(style.yellow("  There are no warehouses yet."))
            return
        }
        terminal.writeLines(
            style.table(
                headers = listOf("#", "ID", "Name", "Zone"),
                rows = warehouses.mapIndexed { index, warehouse ->
                    listOf((index + 1).toString(), warehouse.id, warehouse.name, warehouse.regionalZone)
                },
                rightAligned = setOf(0)
            )
        )
        terminal.writeLine()
    }

    override fun showSkippedRoutes(routeIds: List<String>) {
        terminal.writeLine(
            style.yellow("  ${style.glyphs.warning} ${routeIds.size} route(s) point to an unknown warehouse and were ignored: ${routeIds.joinToString()}")
        )
    }

    override fun showShortestRoute(plan: RoutePlan) {
        terminal.writeLines(
            style.box(
                title = style.green("${style.glyphs.success} Shortest route"),
                lines = listOf(
                    plan.stops.joinToString(" ${style.glyphs.arrow} ") { it.name },
                    "",
                    "Total distance  ${style.bold(formatNumber(plan.totalDistanceKm))} km",
                    "Stops           ${plan.stops.size}   Legs  ${plan.legs.size}"
                )
            )
        )
        if (plan.legs.isNotEmpty()) {
            terminal.writeLines(
                style.table(
                    headers = listOf("From", "To", "Distance (km)"),
                    rows = plan.legs.map { listOf(it.from.name, it.to.name, formatNumber(it.distanceKm)) },
                    rightAligned = setOf(2)
                )
            )
        }
    }

    override fun showNoRouteFound(origin: Warehouse, destination: Warehouse) {
        terminal.writeLine(
            style.yellow("  ${style.glyphs.warning} No route exists from ${origin.name} to ${destination.name}.")
        )
        terminal.writeLine("  " + style.hint("Routes are one-way: check that a road leads from the first warehouse to the second."))
    }

    private companion object {
        const val FROM_PROMPT = "From (number or id, Enter to cancel): "
        const val TO_PROMPT = "To   (number or id, Enter to cancel): "
    }
}
