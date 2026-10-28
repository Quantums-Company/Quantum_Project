package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.result.TelemetrySearchResult
import org.bytebloom.logiroute.presentation.SearchTraceSummary
import org.bytebloom.logiroute.presentation.presenter.TelemetrySearchPresenter
import org.bytebloom.logiroute.presentation.screens.TelemetrySearchScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase

/** The terminal implementation of [TelemetrySearchScreen]: prints a timed log trace. */
class TelemetrySearchTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (TelemetrySearchScreen) -> TelemetrySearchPresenter
) : TerminalScreenBase(terminal, style, uiScope), TelemetrySearchScreen {

    private val presenter by lazy { presenterFactory(this) }

    override suspend fun show() {
        terminal.writeLine()
        terminal.writeLine(
            style.bold(style.cyan("Debounced telemetry search")) +
                    style.dim("   simulated operator typing, debounce 300 ms")
        )
        terminal.writeLine()
        presenter.onRunDemo().join()
    }

    override fun showKeystroke(query: String, elapsedMillis: Long) =
        writeEvent(elapsedMillis, style.dim("KEY   "), quoted(query))

    override fun showSearchStarted(query: String, elapsedMillis: Long) =
        writeEvent(elapsedMillis, style.yellow("SEARCH"), "${quoted(query)} sent to the server")

    override fun showSearchCancelled(query: String, elapsedMillis: Long) =
        writeEvent(elapsedMillis, style.red("CANCEL"), "${quoted(query)} cancelled before it finished")

    override fun showSearchResult(result: TelemetrySearchResult, elapsedMillis: Long) {
        val vehicleIds = result.matches.joinToString { it.vehicleId }.ifEmpty { "no vehicles" }
        writeEvent(
            elapsedMillis,
            style.green("RESULT"),
            "${quoted(result.query)} ${style.glyphs.arrow} ${result.matches.size} match(es): $vehicleIds"
        )
    }

    override fun showSummary(summary: SearchTraceSummary) {
        terminal.writeLine()
        terminal.writeLine(
            style.green("${style.glyphs.success} ${summary.keystrokes} keystrokes") +
                    " ${style.glyphs.arrow} ${summary.searchesStarted} searches started, " +
                    "${summary.searchesCancelled} cancelled, ${summary.searchesCompleted} completed"
        )
    }

    private fun writeEvent(elapsedMillis: Long, tag: String, text: String) {
        val time = style.dim("[" + elapsedMillis.toString().padStart(TIME_WIDTH) + " ms]")
        terminal.writeLine("$time $tag  $text")
    }

    private fun quoted(query: String) = "\"$query\""

    private companion object {
        const val TIME_WIDTH = 5
    }
}