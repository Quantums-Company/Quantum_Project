package org.bytebloom.logiroute.presentation.ui.console.screen

import org.bytebloom.logiroute.presentation.model.RoutePlanUi
import org.bytebloom.logiroute.presentation.model.WarehouseUi
import org.bytebloom.logiroute.presentation.presenter.RoutePresenter
import org.bytebloom.logiroute.presentation.presenter.RouteStatus
import org.bytebloom.logiroute.presentation.ui.console.ConsoleStyle
import org.bytebloom.logiroute.presentation.ui.console.Terminal
import org.bytebloom.logiroute.presentation.ui.console.widget.box
import org.bytebloom.logiroute.presentation.ui.console.widget.errorBox
import org.bytebloom.logiroute.presentation.ui.console.widget.formatNumber
import org.bytebloom.logiroute.presentation.ui.console.widget.table
import org.bytebloom.logiroute.presentation.ui.console.writeLines

class RouteScreen(
    private val presenter: RoutePresenter,
    private val terminal: Terminal,
    private val style: ConsoleStyle
) : ConsoleScreen {

    override suspend fun show() {
        presenter.load()
        val loaded = terminal.awaitWithSpinner(style, "Loading warehouses and routes…", presenter.state) {
            it.status == RouteStatus.READY || it.status == RouteStatus.FAILED
        }

        val loadError = loaded.error
        if (loaded.status == RouteStatus.FAILED && loadError != null) {
            terminal.writeLines(style.errorBox(loadError))
            return
        }
        if (loaded.warehouses.size < MIN_WAREHOUSES) {
            terminal.writeLine(style.yellow("At least two warehouses are needed to plan a route."))
            return
        }
        if (loaded.skippedRouteIds.isNotEmpty()) {
            terminal.writeLine(style.yellow("Skipped routes with unknown warehouses: ${loaded.skippedRouteIds.joinToString()}"))
        }

        showWarehouses(loaded.warehouses)
        val origin = terminal.chooseFrom("From (number or id, Enter to cancel): ", loaded.warehouses, { it.id }, style) ?: return
        val destination = terminal.chooseFrom("To   (number or id, Enter to cancel): ", loaded.warehouses, { it.id }, style) ?: return

        presenter.calculate(origin.id, destination.id)
        val result = terminal.awaitWithSpinner(style, "Calculating the shortest route…", presenter.state) {
            it.status == RouteStatus.FOUND || it.status == RouteStatus.NO_ROUTE || it.status == RouteStatus.FAILED
        }

        val plan = result.plan
        val error = result.error
        when {
            result.status == RouteStatus.FOUND && plan != null -> renderPlan(plan)
            result.status == RouteStatus.FAILED && error != null -> terminal.writeLines(style.errorBox(error))
            else -> terminal.writeLine(style.yellow("No route exists between ${origin.name} and ${destination.name}."))
        }
    }

    private fun showWarehouses(warehouses: List<WarehouseUi>) {
        terminal.writeLine()
        terminal.writeLines(
            style.table(
                headers = listOf("#", "ID", "Name", "Zone"),
                rows = warehouses.mapIndexed { index, it -> listOf((index + 1).toString(), it.id, it.name, it.zone) },
                rightAligned = setOf(0)
            )
        )
        terminal.writeLine()
    }

    private fun renderPlan(plan: RoutePlanUi) {
        val arrow = " ${style.cyan(style.glyphs.arrow)} "
        terminal.writeLine()
        terminal.writeLines(
            style.box(
                title = style.green("${style.glyphs.success} Shortest route"),
                lines = listOf(
                    plan.stops.joinToString(arrow) { style.bold(it.name) },
                    "",
                    "Total distance  ${style.bold(formatNumber(plan.totalDistanceKm))} km",
                    "Stops           ${plan.stops.size}   Legs  ${plan.hops}"
                )
            )
        )
        if (plan.legs.isNotEmpty()) {
            terminal.writeLines(
                style.table(
                    headers = listOf("From", "To", "Distance (km)"),
                    rows = plan.legs.map { listOf(it.fromName, it.toName, formatNumber(it.distanceKm)) },
                    rightAligned = setOf(2)
                )
            )
        }
    }

    private companion object {
        const val MIN_WAREHOUSES = 2
    }
}
