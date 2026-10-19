package org.bytebloom.logiroute.ui.terminal.widget

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private const val BOX_PADDING = 1
private const val SPINNER_FRAME_MILLIS = 90L
private const val FULL_PERCENT = 100.0
private const val HEALTHY_FUEL_PERCENT = 50.0
private const val LOW_FUEL_PERCENT = 20.0
private const val DEFAULT_BAR_WIDTH = 10

fun padVisible(text: String, width: Int, alignRight: Boolean = false): String {
    val padding = " ".repeat((width - ConsoleStyle.visibleLength(text)).coerceAtLeast(0))
    return if (alignRight) padding + text else text + padding
}

/** A rounded box with a title: `╭─ Title ───╮ … ╰──────────╯`. */
fun ConsoleStyle.box(title: String, lines: List<String>): List<String> {
    val g = glyphs
    val contentWidth = maxOf(
        ConsoleStyle.visibleLength(title) + TITLE_MIN_EXTRA,
        lines.maxOfOrNull { ConsoleStyle.visibleLength(it) } ?: 0
    )
    val inner = contentWidth + BOX_PADDING * 2
    val top = g.topLeft + g.horizontal + " " + bold(title) + " " +
            g.horizontal.repeat(inner - ConsoleStyle.visibleLength(title) - TITLE_FRAME) + g.topRight
    val body = lines.map { g.vertical + " " + padVisible(it, contentWidth) + " " + g.vertical }
    val bottom = g.bottomLeft + g.horizontal.repeat(inner) + g.bottomRight
    return listOf(top) + body + listOf(bottom)
}

private const val TITLE_MIN_EXTRA = 4
private const val TITLE_FRAME = 3

/** A table with a header row. Columns listed in [rightAligned] (numbers) are aligned to the right. */
fun ConsoleStyle.table(
    headers: List<String>,
    rows: List<List<String>>,
    rightAligned: Set<Int> = emptySet()
): List<String> {
    val g = glyphs
    val widths = headers.indices.map { column ->
        maxOf(
            ConsoleStyle.visibleLength(headers[column]),
            rows.maxOfOrNull { ConsoleStyle.visibleLength(it[column]) } ?: 0
        )
    }

    fun rule(left: String, middle: String, right: String) =
        left + widths.joinToString(middle) { g.horizontal.repeat(it + 2) } + right

    fun line(cells: List<String>) =
        g.vertical + cells.mapIndexed { column, cell ->
            " " + padVisible(cell, widths[column], alignRight = column in rightAligned) + " "
        }.joinToString(g.vertical) + g.vertical

    return buildList {
        add(rule(g.topLeft, g.teeDown, g.topRight))
        add(line(headers.map { bold(it) }))
        add(rule(g.teeRight, g.cross, g.teeLeft))
        rows.forEach { add(line(it)) }
        add(rule(g.bottomLeft, g.teeUp, g.bottomRight))
    }
}

/** `████████░░ 82%` — green when healthy, yellow when getting low, red when critical. */
fun ConsoleStyle.percentBar(percent: Double, width: Int = DEFAULT_BAR_WIDTH): String {
    val clamped = percent.coerceIn(0.0, FULL_PERCENT)
    val filled = (clamped / FULL_PERCENT * width).roundToInt()
    val bar = glyphs.barFilled.repeat(filled) + glyphs.barEmpty.repeat(width - filled)
    val coloured = when {
        clamped >= HEALTHY_FUEL_PERCENT -> green(bar)
        clamped >= LOW_FUEL_PERCENT -> yellow(bar)
        else -> red(bar)
    }
    return coloured + " " + padVisible(formatNumber(clamped, 0) + "%", PERCENT_WIDTH, alignRight = true)
}

private const val PERCENT_WIDTH = 4

fun formatNumber(value: Double, decimals: Int = 2): String =
    String.format(Locale.ROOT, "%.${decimals}f", value)

/**
 * Shows a spinner next to [message] while [block] runs, then erases it.
 * The spinner is a child coroutine, so it is canceled even if [block] throws or the app is closed.
 */
suspend fun <T> Terminal.withSpinner(
    style: ConsoleStyle,
    message: String,
    block: suspend CoroutineScope.() -> T
): T = coroutineScope {
    val spinner = launch {
        var frame = 0
        while (isActive) {
            write("\r" + style.cyan(style.glyphs.spinner[frame % style.glyphs.spinner.size]) + " " + message)
            frame++
            delay(SPINNER_FRAME_MILLIS.milliseconds)
        }
    }
    try {
        block()
    } finally {
        spinner.cancelAndJoin()
        clearLine()
    }
}
