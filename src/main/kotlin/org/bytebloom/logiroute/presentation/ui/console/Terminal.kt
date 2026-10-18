package org.bytebloom.logiroute.presentation.ui.console

/**
 * The only door between the console UI and the outside world.
 * Production uses [ConsoleTerminal]; tests use a fake that records what was written.
 */
interface Terminal {
    fun write(text: String)
    fun writeLine(text: String = "")
    fun clearLine()

    /** Suspends until the user presses Enter. Returns null when the input ends (Ctrl+D / closed pipe). */
    suspend fun readLine(): String?
}

suspend fun Terminal.prompt(label: String): String? {
    write(label)
    return readLine()?.trim()
}

fun Terminal.writeLines(lines: List<String>) = lines.forEach(::writeLine)
