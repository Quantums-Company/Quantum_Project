package org.bytebloom.logiroute.ui.terminal

/**
 * Shows a small menu inside a screen and keeps running the chosen actions until the user goes back
 * (0, a blank answer, a back word, or the end of the input).
 */
suspend fun Terminal.runActionMenu(style: ConsoleStyle, title: String, actions: List<MenuAction>) {
    while (true) {
        writeLine()
        writeLine(style.bold(title))
        actions.forEachIndexed { index, action ->
            writeLine("  ${style.cyan((index + 1).toString().padStart(2))}  ${action.label}")
        }
        writeLine("  ${style.cyan(" 0")}  Back")

        val answer = prompt(style.bold("Choose an action › ")) ?: return
        val key = answer.lowercase()
        if (key.isBlank() || key in BACK_WORDS) return

        val action = key.toIntOrNull()?.let { actions.getOrNull(it - 1) }
            ?: actions.firstOrNull { key in it.aliases }

        if (action == null) {
            writeLine(style.yellow("  '$answer' is not an action — type an index from the list, or 0 to go back."))
        } else {
            action.run()
        }
    }
}

private val BACK_WORDS = setOf("0", "back", "b")
