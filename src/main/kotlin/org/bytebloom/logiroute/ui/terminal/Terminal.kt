package org.bytebloom.logiroute.ui.terminal

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