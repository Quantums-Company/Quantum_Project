package org.bytebloom.logiroute.ui.terminal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val SPINNER_FRAME_MILLIS = 90L

/**
 * An animated "⠋ Loading…" line.
 *
 * [start] and [stop] are called by the presenter (any thread) through `showLoadingIndicator`.
 * Writing a frame and erasing the line happen under one lock, so a late frame can never appear
 * after [stop] has cleaned the line.
 */
class TerminalSpinner(
    private val terminal: Terminal,
    private val style: ConsoleStyle,
    private val scope: CoroutineScope
) {
    private val lock = Any()
    private var running = false
    private var animation: Job? = null

    fun start(message: String) {
        synchronized(lock) {
            if (running) return
            running = true
        }
        animation = scope.launch {
            var frame = 0
            while (isActive) {
                synchronized(lock) {
                    if (running) {
                        val glyph = style.glyphs.spinner[frame % style.glyphs.spinner.size]
                        terminal.write("\r" + style.cyan(glyph) + " " + message)
                    }
                }
                frame++
                delay(SPINNER_FRAME_MILLIS.milliseconds)
            }
        }
    }

    fun stop() {
        synchronized(lock) {
            if (!running) return
            running = false
            terminal.clearLine()
        }
        animation?.cancel()
        animation = null
    }
}
