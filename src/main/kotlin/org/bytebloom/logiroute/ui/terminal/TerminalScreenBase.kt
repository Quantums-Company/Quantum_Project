package org.bytebloom.logiroute.ui.terminal

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.presentation.screens.BaseScreen
import org.bytebloom.logiroute.ui.terminal.widget.errorBox

/**
 * Shared behavior of every terminal UI class: the loading indicator and the error box.
 * A concrete screen (for example `ShortestRouteTerminalUI`) extends this class and also
 * implements its own screen interface (`ShortestRouteScreen`).
 */
abstract class TerminalScreenBase(
    protected val terminal: Terminal,
    protected val style: ConsoleStyle,
    uiScope: CoroutineScope
) : BaseScreen, TerminalScreen {

    private val spinner = TerminalSpinner(terminal, style, uiScope)

    /** The text next to the spinner. A screen changes it before it triggers a new kind of work. */
    @Volatile
    protected var loadingMessage: String = DEFAULT_LOADING_MESSAGE

    override fun showLoadingIndicator(isLoading: Boolean) =
        if (isLoading) spinner.start(loadingMessage) else spinner.stop()

    override fun showErrorMessage(errorMessage: String) =
        terminal.writeLines(style.errorBox(errorMessage))

    protected fun heading(text: String) {
        terminal.writeLine()
        terminal.writeLine(style.bold(text))
    }

    protected fun printSuccess(text: String) =
        terminal.writeLine(style.green("${style.glyphs.success} $text"))

    protected fun printWarning(text: String) =
        terminal.writeLine(style.yellow("  ${style.glyphs.warning} $text"))

    protected fun printLines(lines: List<String>) = terminal.writeLines(lines)

    private companion object {
        const val DEFAULT_LOADING_MESSAGE = "Working…"
    }
}