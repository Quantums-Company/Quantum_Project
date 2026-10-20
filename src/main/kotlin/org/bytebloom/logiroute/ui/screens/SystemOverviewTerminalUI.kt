package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.presentation.presenter.SystemOverviewPresenter
import org.bytebloom.logiroute.presentation.SystemSummary
import org.bytebloom.logiroute.presentation.screens.SystemOverviewScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.packageTable
import org.bytebloom.logiroute.ui.terminal.widget.padVisible
import org.bytebloom.logiroute.ui.terminal.widget.routeTable
import org.bytebloom.logiroute.ui.terminal.widget.vehicleTable
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

/** The terminal implementation of [SystemOverviewScreen]. */
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

    override fun showSummary(summary: SystemSummary) {
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
        section("Warehouses", summary.warehouses.size) { style.warehouseTable(summary.warehouses) }
        section("Vehicles", summary.vehicles.size) { style.vehicleTable(summary.vehicles) }
        section("Packages", summary.packages.size) { style.packageTable(summary.packages) }
        section("Routes", summary.routes.size) { style.routeTable(summary.routes) }
    }

    private fun count(label: String, value: Int) = padVisible(label, LABEL_WIDTH) + value

    private fun section(title: String, size: Int, table: () -> List<String>) {
        terminal.writeLine()
        terminal.writeLine(style.bold("$title  ($size)"))
        if (size == 0) terminal.writeLine(style.dim("  nothing yet")) else printLines(table())
    }

    private companion object {
        const val LABEL_WIDTH = 12
    }
}
