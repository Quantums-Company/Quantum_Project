package org.bytebloom.logiroute.ui.terminal

import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.errorBox
import org.bytebloom.logiroute.ui.terminal.widget.hint

/**
 * The terminal "shell": draws the menu, reads the choice, opens a screen.
 *
 * It runs on a coroutine and never blocks a thread while waiting for the keyboard,
 * so cancelling the surrounding scope ends it immediately.
 */
class TerminalApp(
    private val terminal: Terminal,
    private val style: ConsoleStyle,
    private val sections: List<MenuSection>
) {
    private val entries: List<MenuEntry> = sections.flatMap { it.entries }

    @Suppress("LoopWithTooManyJumpStatements")
    suspend fun run() {
        printBanner()
        while (true) {
            printMenu()
            val answer = terminal.prompt(style.bold("Choose an option › ")) ?: break
            val key = answer.lowercase()

            if (key in EXIT_WORDS) break

            val entry = findEntry(key)
            if (entry == null) {
                terminal.writeLine(style.yellow("  '$answer' is not an option" +
                        " — type a number from the menu, or 0 to exit."))
            } else {
                open(entry)
            }
        }
        terminal.writeLine()
        terminal.writeLine(style.green("${style.glyphs.success} Goodbye" +
                " — all background work has been stopped."))
    }

    private fun findEntry(key: String): MenuEntry? =
        key.toIntOrNull()?.let { number -> entries.getOrNull(number - 1) }
            ?: entries.firstOrNull { key in it.aliases }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun open(entry: MenuEntry) {
        try {
            entry.screen.show()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (failure: Exception) {
            // A screen already reports expected errors itself; this only protects the menu from a crash.
            terminal.writeLines(style.errorBox(failure.message ?: failure::class.simpleName.orEmpty()))
        }
    }

    private fun printBanner() {
        terminal.writeLine()
        terminal.writeLines(
            style.box(
                title = style.cyan(BANNER_TITLE),
                lines = listOf(BANNER_TAGLINE, style.hint(BANNER_HINT))
            )
        )
    }

    private fun printMenu() {
        terminal.writeLine()
        var number = 1
        sections.forEach { section ->
            section.title?.let { terminal.writeLine("  " + style.bold(style.dim(it))) }
            section.entries.forEach { entry ->
                val key = number.toString().padStart(NUMBER_WIDTH)
                terminal.writeLine("  ${style.cyan(key)}  ${entry.title}")
                number++
            }
        }
        terminal.writeLine("  ${style.cyan("0".padStart(NUMBER_WIDTH))}  Exit")
        terminal.writeLine()
    }

    private companion object {
        const val BANNER_TITLE = "LogiRoute"
        const val BANNER_TAGLINE = "Smart logistics: routing · cargo · live telemetry"
        const val BANNER_HINT = "Type a number or a word, 0 to exit"
        const val NUMBER_WIDTH = 2
        val EXIT_WORDS = setOf("0", "exit", "quit", "q")
    }
}