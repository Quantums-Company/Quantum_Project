
package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.presentation.SystemSummary
import org.bytebloom.logiroute.presentation.presenter.SystemOverviewPresenter
import org.bytebloom.logiroute.presentation.screens.SystemOverviewScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.widget.DEFAULT_ROW_LIMIT
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.packageTable
import org.bytebloom.logiroute.ui.terminal.widget.padVisible
import org.bytebloom.logiroute.ui.terminal.widget.routeTable
import org.bytebloom.logiroute.ui.terminal.widget.vehicleTable
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

class SystemOverviewTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (SystemOverviewScreen) -> SystemOverviewPresenter
) : TerminalScreenBase(terminal, style, uiScope), SystemOverviewScreen {

    private val presenter by lazy { presenterFactory(this) }

    override suspend fun show() {
        loadingMessage = "Loading the system data…"
        presenter.onViewReady().join()
    }

    override suspend fun showSummary(summary: SystemSummary) {
        terminal.writeLine()

        printLines(
            style.box(
                title = "System overview",
                lines = listOf(
                    count("Warehouses", summary.warehouses.size),
                    count("Routes", summary.routes.size),
                    count("Vehicles", summary.vehicles.size),
                    count("Packages", summary.packages.size)
                )
            )
        )

        val hasHiddenRows = listOf(
            summary.warehouses.size,
            summary.vehicles.size,
            summary.packages.size,
            summary.routes.size
        ).any { it > DEFAULT_ROW_LIMIT }

        val showAll = if (hasHiddenRows) {
            terminal.writeLine()
            terminal.write("Show ALL rows in all four tables? [A/N]: ")
            terminal.readLine()?.trim().equals("A", ignoreCase = true)
        } else {
            false
        }

        section(
            title = "Warehouses",
            size = summary.warehouses.size,
            showAll = showAll,
            compactTable = { style.warehouseTable(summary.warehouses) },
            fullTable = { style.warehouseTable(summary.warehouses, limit = null) }
        )

        section(
            title = "Vehicles",
            size = summary.vehicles.size,
            showAll = showAll,
            compactTable = { style.vehicleTable(summary.vehicles) },
            fullTable = { style.vehicleTable(summary.vehicles, limit = null) }
        )

        section(
            title = "Packages",
            size = summary.packages.size,
            showAll = showAll,
            compactTable = { style.packageTable(summary.packages) },
            fullTable = { style.packageTable(summary.packages, limit = null) }
        )

        section(
            title = "Routes",
            size = summary.routes.size,
            showAll = showAll,
            compactTable = { style.routeTable(summary.routes) },
            fullTable = { style.routeTable(summary.routes, limit = null) }
        )
    }

    private fun count(label: String, value: Int) =
        padVisible(label, LABEL_WIDTH) + value

    private fun section(
        title: String,
        size: Int,
        showAll: Boolean,
        compactTable: () -> List<String>,
        fullTable: () -> List<String>
    ) {
        terminal.writeLine()
        terminal.writeLine(style.bold("$title  ($size)"))

        when {
            size == 0 -> {
                terminal.writeLine(style.dim("  nothing yet"))
            }

            showAll -> {
                printLines(fullTable())
            }

            else -> {
                printLines(compactTable())
            }
        }
    }

    private companion object {
        const val LABEL_WIDTH = 12
    }
}
