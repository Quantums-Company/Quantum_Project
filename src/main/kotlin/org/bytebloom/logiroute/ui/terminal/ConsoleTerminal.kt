package org.bytebloom.logiroute.ui.terminal

import kotlinx.coroutines.channels.Channel
import java.io.BufferedReader
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import kotlin.concurrent.thread

/**
 * Real keyboard and screen.
 *
 * Reading a line from stdin blocks and cannot be interrupted, so it happens on ONE daemon thread
 * that pushes lines into a channel. The coroutine side just `receive()`s from the channel:
 * that is cancellable and never blocks a coroutine thread. Because the reader thread is a daemon,
 * it can never keep the JVM alive after the application has finished.
 */
class ConsoleTerminal(
    input: InputStream = System.`in`,
    private val output: PrintStream = PrintStream(FileOutputStream(FileDescriptor.out),
        true, Charsets.UTF_8)
) : Terminal, AutoCloseable {

    private val lines = Channel<String?>(Channel.UNLIMITED)

    init {
        val reader = BufferedReader(InputStreamReader(input, Charsets.UTF_8))
        thread(isDaemon = true, name = READER_THREAD_NAME) {
            try {
                while (true) {
                    val line = reader.readLine()
                    lines.trySend(line)
                    if (line == null) break
                }
            } finally {
                lines.close()
            }
        }
    }

    override fun write(text: String) {
        output.print(text)
        output.flush()
    }

    override fun writeLine(text: String) {
        output.println(text)
        output.flush()
    }

    /** Overwrites the line with spaces: unlike the "erase line" escape code, this works in every console. */
    override fun clearLine() = write("\r" + " ".repeat(CLEAR_WIDTH) + "\r")

    override suspend fun readLine(): String? = lines.receiveCatching().getOrNull()

    override fun close() {
        lines.close()
        output.flush()
    }

    private companion object {
        const val READER_THREAD_NAME = "logiroute-console-input"
        const val CLEAR_WIDTH = 70
    }
}
