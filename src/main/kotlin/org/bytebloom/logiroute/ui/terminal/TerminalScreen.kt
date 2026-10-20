package org.bytebloom.logiroute.ui.terminal

/** One screen of the terminal application: the menu opens it with [show] and waits until it returns. */
interface TerminalScreen {
    suspend fun show()
}

