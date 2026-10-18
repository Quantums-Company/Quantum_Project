package org.bytebloom.logiroute.presentation.ui.console.screen

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import org.bytebloom.logiroute.presentation.ui.console.ConsoleStyle
import org.bytebloom.logiroute.presentation.ui.console.Terminal
import org.bytebloom.logiroute.presentation.ui.console.prompt
import org.bytebloom.logiroute.presentation.ui.console.widget.withSpinner

/** One screen of the console application. */
interface ConsoleScreen {
    suspend fun show()
}

/** Shows a spinner until the presenter's state satisfies [isSettled], then returns that state. */
internal suspend fun <S> Terminal.awaitWithSpinner(
    style: ConsoleStyle,
    message: String,
    state: StateFlow<S>,
    isSettled: (S) -> Boolean
): S = withSpinner(style, message) { state.first(isSettled) }

/**
 * Lets the user pick an item by its list number (1, 2, 3…) or by its id.
 * A blank answer (just Enter) cancels and returns null.
 */
internal suspend fun <T> Terminal.chooseFrom(
    label: String,
    items: List<T>,
    idOf: (T) -> String,
    style: ConsoleStyle
): T? {
    while (true) {
        val answer = prompt(label) ?: return null
        if (answer.isBlank()) return null

        val chosen = answer.toIntOrNull()?.let { number -> items.getOrNull(number - 1) }
            ?: items.firstOrNull { idOf(it).equals(answer, ignoreCase = true) }

        if (chosen != null) return chosen
        writeLine(style.yellow("  '$answer' is not in the list — type a number or an id, or press Enter to cancel."))
    }
}

