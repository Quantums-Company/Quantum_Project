package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.performance.TreePerformanceReport
import org.bytebloom.logiroute.presentation.presenter.AnalyticsPresenter
import org.bytebloom.logiroute.presentation.screens.AnalyticsScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.askInt
import org.bytebloom.logiroute.ui.terminal.widget.table

/** The terminal implementation of [AnalyticsScreen]. */
class AnalyticsTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (AnalyticsScreen) -> AnalyticsPresenter
) : TerminalScreenBase(terminal, style, uiScope), AnalyticsScreen {

    private val presenter by lazy { presenterFactory(this) }

    override suspend fun show() {
        heading("Analytics — BST vs AVL tree search")
        terminal.writeLine("  Fills both trees with tracking ids (PKG-000001 …)" +
                " and counts the steps of a search.")
        val count = terminal.askInt("How many packages? (Enter to go back): ", style) ?: return
        loadingMessage = "Building the trees…"
        presenter.onAnalyze(count).join()
    }

    override fun showReport(report: TreePerformanceReport) {
        terminal.writeLine()
        terminal.writeLine(style.bold("  ${report.totalPackages} tracking ids in each tree"))
        printLines(
            style.table(
                headers = listOf("Tracking id", "BST steps", "AVL steps"),
                rows = report.results.map {
                    listOf(it.trackingId, it.binarySearchTreeSteps.toString(), it.avlTreeSteps.toString())
                },
                rightAligned = setOf(1, 2)
            )
        )
        terminal.writeLine(style.dim("  Ids arrive in sorted order," +
                " so the plain BST degenerates into a list; the AVL tree stays balanced."))
    }
}
