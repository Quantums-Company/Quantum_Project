package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.presentation.presenter.RouteAnalysisPresenter
import org.bytebloom.logiroute.presentation.screens.RouteAnalysisScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.MenuAction
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.runActionMenu
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.table
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

/** The terminal implementation of [RouteAnalysisScreen]. */
class RouteAnalysisTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (RouteAnalysisScreen) -> RouteAnalysisPresenter
) : TerminalScreenBase(terminal, style, uiScope), RouteAnalysisScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    override suspend fun show() {
        heading("Route analysis")
        warehouses = emptyList()
        loadingMessage = "Loading warehouses and routes…"
        presenter.onViewReady().join()
        if (warehouses.isEmpty()) return

        terminal.runActionMenu(
            style,
            "Route analysis — what do you want to do?",
            listOf(
                MenuAction("Route with the fewest stops",
                    setOf("hops", "fewest")) { fewestHops() },
                MenuAction("Is there a link between two warehouses?",
                    setOf("link", "verify")) { verifyLink() },
                MenuAction("Shortest distance between every pair",
                    setOf("pairs", "all")) { allPairs() }
            )
        )
    }

    private suspend fun fewestHops() {
        val (origin, destination) = pickPair() ?: return
        loadingMessage = "Searching for the fewest stops…"
        presenter.onFindFewestHops(origin.id, destination.id).join()
    }

    private suspend fun verifyLink() {
        val (origin, destination) = pickPair() ?: return
        loadingMessage = "Checking the link…"
        presenter.onVerifyLink(origin.id, destination.id).join()
    }

    private suspend fun allPairs() {
        loadingMessage = "Calculating every distance…"
        presenter.onShowAllPairs().join()
    }

    private suspend fun pickPair(): Pair<Warehouse, Warehouse>? {
        printLines(style.warehouseTable(warehouses, numbered = true))
        val origin = terminal.chooseFrom("From (index or id, Enter to cancel): ",
            warehouses, Warehouse::id, style) ?: return null
        val destination = terminal.chooseFrom("To   (index or id, Enter to cancel): ",
            warehouses, Warehouse::id, style) ?: return null
        return origin to destination
    }

    override fun showWarehouses(warehouses: List<Warehouse>) {
        this.warehouses = warehouses
        if (warehouses.isEmpty()) printWarning("There are no warehouses yet.")
    }

    override fun showFewestHopsRoute(path: List<Warehouse>) {
        printLines(
            style.box(
                title = style.green("${style.glyphs.success} Fewest stops"),
                lines = listOf(
                    path.joinToString(" ${style.glyphs.arrow} ") { it.name },
                    "",
                    "Stops  ${path.size}   Hops  ${path.size - 1}"
                )
            )
        )
    }

    override fun showNoRouteFound(origin: Warehouse, destination: Warehouse) =
        printWarning("No route exists from ${origin.name} to ${destination.name}.")

    override fun showHubLink(origin: Warehouse, destination: Warehouse, isLinked: Boolean) {
        if (isLinked) {
            printSuccess("${origin.name} ${style.glyphs.arrow}" +
                    " ${destination.name} are linked by at least one route.")
        } else {
            printWarning("${origin.name} ${style.glyphs.arrow}" +
                    " ${destination.name} are NOT linked (or are the same warehouse).")
        }
    }

    override fun showAllPairsDistances(distances: Map<Warehouse, Map<Warehouse, Double>>) {
        val columns = distances.keys.sortedBy { it.id }
        terminal.writeLine()
        terminal.writeLine(style.bold("Shortest distance in km (rows: from, columns: to)"))
        printLines(
            style.table(
                headers = listOf("From \\ To") + columns.map { it.id },
                rows = columns.map { from ->
                    listOf(from.id) + columns.map { to ->
                        distanceText(distances.getValue(from).getValue(to)) }
                },
                rightAligned = columns.indices.map { it + 1 }.toSet()
            )
        )
        terminal.writeLine(style.dim("  ${unreachable()} = no route"))
    }

    private fun distanceText(distance: Double): String =
        if (distance.isInfinite()) unreachable() else formatNumber(distance, 1)

    private fun unreachable(): String = if (style.unicode) UNREACHABLE else ASCII_UNREACHABLE

    private companion object {
        const val UNREACHABLE = "–"
        const val ASCII_UNREACHABLE = "-"
    }
}

