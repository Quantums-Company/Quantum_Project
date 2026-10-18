package org.bytebloom.logiroute.presentation.ui.console

import kotlinx.coroutines.channels.Channel

/** A keyboard and screen that live in memory. Everything the app prints ends up in [output]. */
class FakeTerminal : Terminal {
    private val input = Channel<String?>(Channel.UNLIMITED)
    private val written = StringBuilder()

    val output: String get() = written.toString()

    /** What the user types, followed by Enter. */
    fun type(line: String) {
        input.trySend(line)
    }

    /** The user closes the input (Ctrl+D). */
    fun endInput() {
        input.close()
    }

    override fun write(text: String) {
        written.append(text)
    }

    override fun writeLine(text: String) {
        written.append(text).append('\n')
    }

    override fun clearLine() {
        written.append('\r')
    }

    override suspend fun readLine(): String? = input.receiveCatching().getOrNull()
}
