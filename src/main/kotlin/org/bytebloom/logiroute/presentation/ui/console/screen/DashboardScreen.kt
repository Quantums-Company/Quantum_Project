package org.bytebloom.logiroute.presentation.ui.console.screen

import org.bytebloom.logiroute.presentation.model.LoadStatus
import org.bytebloom.logiroute.presentation.model.PriorityUi
import org.bytebloom.logiroute.presentation.presenter.DashboardPresenter
import org.bytebloom.logiroute.presentation.presenter.DashboardState
import org.bytebloom.logiroute.presentation.ui.console.ConsoleStyle
import org.bytebloom.logiroute.presentation.ui.console.Terminal
import org.bytebloom.logiroute.presentation.ui.console.widget.box
import org.bytebloom.logiroute.presentation.ui.console.widget.errorBox
import org.bytebloom.logiroute.presentation.ui.console.widget.formatNumber
import org.bytebloom.logiroute.presentation.ui.console.widget.table
import org.bytebloom.logiroute.presentation.ui.console.writeLines

class DashboardScreen(
    private val presenter: DashboardPresenter,
    private val terminal: Terminal,
    private val style: ConsoleStyle
) : ConsoleScreen {

    override suspend fun show() {
        presenter.load()
        val state = terminal.awaitWithSpinner(style, "Loading fleet data…", presenter.state) {
            it.status == LoadStatus.READY || it.status == LoadStatus.FAILED
        }

        val error = state.error
        if (state.status == LoadStatus.FAILED && error != null) {
            terminal.writeLines(style.errorBox(error))
        } else {
            render(state)
        }
    }

    private fun render(state: DashboardState) {
        terminal.writeLine()
        terminal.writeLines(
            style.box(
                title = "System overview",
                lines = listOf(
                    "Warehouses  ${style.bold(state.warehouses.size.toString())}",
                    "Routes      ${style.bold(state.routes.size.toString())}",
                    "Vehicles    ${style.bold(state.vehicles.size.toString())}",
                    "Packages    ${style.bold(state.packages.size.toString())}"
                )
            )
        )
        section("Warehouses", listOf("ID", "Name", "Zone"), state.warehouses.map { listOf(it.id, it.name, it.zone) })
        section(
            "Vehicles",
            listOf("ID", "Capacity (kg)", "Cost / km", "Stationed at"),
            state.vehicles.map {
                listOf(it.id, formatNumber(it.capacityKg, 1), formatNumber(it.costPerKm), it.warehouseName)
            },
            rightAligned = setOf(1, 2)
        )
        section(
            "Packages",
            listOf("ID", "Weight (kg)", "Priority", "From", "To"),
            state.packages.map {
                listOf(it.id, formatNumber(it.weightKg, 1), priorityLabel(it.priority), it.originName, it.destinationName)
            },
            rightAligned = setOf(1)
        )
        section(
            "Routes",
            listOf("ID", "From", "To", "Distance (km)", "Delay (min)"),
            state.routes.map {
                listOf(it.id, it.originName, it.destinationName, formatNumber(it.distanceKm), it.typicalDelayMin.toString())
            },
            rightAligned = setOf(3, 4)
        )
    }

    private fun section(
        title: String,
        headers: List<String>,
        rows: List<List<String>>,
        rightAligned: Set<Int> = emptySet()
    ) {
        terminal.writeLine()
        terminal.writeLine(style.bold(style.cyan(title)) + style.dim("  (${rows.size})"))
        if (rows.isEmpty()) {
            terminal.writeLine(style.dim("  nothing to show"))
            return
        }
        val shown = rows.take(MAX_ROWS)
        terminal.writeLines(style.table(headers, shown, rightAligned))
        if (rows.size > shown.size) {
            terminal.writeLine(style.dim("  … and ${rows.size - shown.size} more"))
        }
    }

    private fun priorityLabel(priority: PriorityUi): String = when (priority) {
        PriorityUi.URGENT -> style.red("URGENT")
        PriorityUi.STANDARD -> style.yellow("STANDARD")
        PriorityUi.LOW -> style.green("LOW")
    }

    private companion object {
        const val MAX_ROWS = 10
    }
}
