package org.bytebloom.logiroute.ui.terminal

/**
 * Small helpers that ask the user one question and give back a typed answer.
 *
 * Common rule: a blank answer (just Enter), or the end of the input, returns `null`.
 * For a required field `null` means "cancel"; for an optional field it means "keep the current value".
 */

private val YES_ANSWERS = setOf("y", "yes")

suspend fun Terminal.askText(label: String): String? =
    prompt(label)?.takeIf { it.isNotBlank() }

suspend fun Terminal.askDouble(label: String, style: ConsoleStyle): Double? {
    while (true) {
        val answer = askText(label) ?: return null
        val number = answer.toDoubleOrNull()
        if (number != null) return number
        writeLine(style.yellow("  '$answer' is not a number — try again or press Enter to skip."))
    }
}

suspend fun Terminal.askInt(label: String, style: ConsoleStyle): Int? {
    while (true) {
        val answer = askText(label) ?: return null
        val number = answer.toIntOrNull()
        if (number != null) return number
        writeLine(style.yellow("  '$answer' is not a whole number — try again or press Enter to skip."))
    }
}

/** Anything other than "y" / "yes" counts as no, so a stray Enter can never confirm a destructive action. */
suspend fun Terminal.confirm(label: String): Boolean =
    prompt("$label (y/N) ")?.lowercase() in YES_ANSWERS

/**
 * Lets the user pick an item by its index (1, 2, 3…) or by its id/name.
 * A blank answer (just Enter) cancels and returns null.
 */
suspend fun <T> Terminal.chooseFrom(
    label: String,
    items: List<T>,
    idOf: (T) -> String,
    style: ConsoleStyle
): T? {
    while (true) {
        val answer = askText(label) ?: return null

        val chosen = answer.toIntOrNull()?.let { number -> items.getOrNull(number - 1) }
            ?: items.firstOrNull { idOf(it).equals(answer, ignoreCase = true) }

        if (chosen != null) return chosen
        writeLine(style.yellow("  '$answer' is not in the list" +
                " — type an index or a name, or press Enter to cancel."))
    }
}
