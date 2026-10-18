package org.bytebloom.logiroute.presentation.ui.console

import org.bytebloom.logiroute.presentation.presenter.AppPresenters
import org.bytebloom.logiroute.presentation.ui.console.screen.ClassificationScreen
import org.bytebloom.logiroute.presentation.ui.console.screen.ConsoleScreen
import org.bytebloom.logiroute.presentation.ui.console.screen.DashboardScreen
import org.bytebloom.logiroute.presentation.ui.console.screen.RouteScreen
import org.bytebloom.logiroute.presentation.ui.console.screen.TelemetryScreen
import org.bytebloom.logiroute.presentation.ui.console.widget.box
import org.bytebloom.logiroute.presentation.ui.console.widget.hint

/**
 * The console "shell": draws the menu, reads the user's choice, opens a screen.
 *
 * It runs on a coroutine and never blocks a thread while waiting for the keyboard,
 * so cancelling the surrounding scope ends it immediately.
 */
class ConsoleApp(
    private val terminal: Terminal,
    private val style: ConsoleStyle,
    presenters: AppPresenters
) {
    private val screens: Map<MenuOption, ConsoleScreen> = mapOf(
        MenuOption.OVERVIEW to DashboardScreen(presenters.dashboard, terminal, style),
        MenuOption.ROUTE to RouteScreen(presenters.route, terminal, style),
        MenuOption.CLASSIFY to ClassificationScreen(presenters.classification, terminal, style),
        MenuOption.TELEMETRY to TelemetryScreen(presenters.telemetry, terminal, style)
    )

    suspend fun run() {
        printBanner()
        while (true) {
            printMenu()
            val answer = terminal.prompt(style.bold("Choose an option › ")) ?: break
            val option = MenuOption.parse(answer)

            when {
                option == MenuOption.EXIT -> break
                option == null -> terminal.writeLine(style.yellow("  '$answer' is not an option — try ${optionKeys()}."))
                else -> screens.getValue(option).show()
            }
        }
        terminal.writeLine()
        terminal.writeLine(style.green("${style.glyphs.success} Goodbye — all background work has been stopped."))
    }

    private fun printBanner() {
        terminal.writeLine()
        terminal.writeLines(
            style.box(
                title = style.cyan("LogiRoute"),
                lines = listOf(
                    "Smart logistics: routing · cargo · live telemetry",
                    style.hint("Type a number or a word (route, knn, live, exit)")
                )
            )
        )
    }

    private fun printMenu() {
        terminal.writeLine()
        MenuOption.entries.forEach { option ->
            terminal.writeLine("  ${style.cyan(option.key)}  ${option.label}")
        }
        terminal.writeLine()
    }

    private fun optionKeys() = MenuOption.entries.joinToString(", ") { it.key }
}
