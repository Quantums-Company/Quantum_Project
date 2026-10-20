package org.bytebloom.logiroute.ui.terminal.widget

import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import java.util.Locale
import kotlin.math.roundToInt

private const val BOX_PADDING = 1
private const val FULL_PERCENT = 100.0
private const val HEALTHY_FUEL_PERCENT = 50.0
private const val LOW_FUEL_PERCENT = 20.0
private const val DEFAULT_BAR_WIDTH = 10

fun padVisible(text: String, width: Int, alignRight: Boolean = false): String {
    val padding = " ".repeat((width - ConsoleStyle.visibleLength(text)).coerceAtLeast(0))
    return if (alignRight) padding + text else text + padding
}

/** A rounded box with a title: `╭─ Title ───╮ … ╰──────────╯`. The frame is dimmed so the content stands out. */
fun ConsoleStyle.box(title: String, lines: List<String>): List<String> {
    val g = glyphs
    val contentWidth = maxOf(
        ConsoleStyle.visibleLength(title) + TITLE_MIN_EXTRA,
        lines.maxOfOrNull { ConsoleStyle.visibleLength(it) } ?: 0
    )
    val inner = contentWidth + BOX_PADDING * 2
    val top = dim(g.topLeft + g.horizontal) + " " + bold(title) + " " +
            dim(g.horizontal.repeat(inner - ConsoleStyle.visibleLength(title)
                    - TITLE_FRAME) + g.topRight)
    val body = lines.map { dim(g.vertical) + " " + padVisible(it, contentWidth)+
            " " + dim(g.vertical) }
    val bottom = dim(g.bottomLeft + g.horizontal.repeat(inner) + g.bottomRight)
    return listOf(top) + body + listOf(bottom)
}

private const val TITLE_MIN_EXTRA = 4
private const val TITLE_FRAME = 3

/** How many rows a long list shows before it says "… and N more". */
const val DEFAULT_ROW_LIMIT = 10

/**
 * A colored table: dimmed frame, bold cyan header. Columns listed in [rightAligned] (numbers) are
 * aligned to the right. [cellColors] paints a whole column (for example the id column).
 *
 * With a [limit], only the first rows are drawn and a last line says how many were left out;
 * [moreHint] is added to that line (for example how to pick a hidden row).
 */
fun ConsoleStyle.table(
    headers: List<String>,
    rows: List<List<String>>,
    rightAligned: Set<Int> = emptySet(),
    cellColors: Map<Int, (String) -> String> = emptyMap(),
    limit: Int? = null,
    moreHint: String = ""
): List<String> {
    val g = glyphs
    val shown = if (limit != null) rows.take(limit) else rows
    val hidden = rows.size - shown.size
    val widths = headers.indices.map { column ->
        maxOf(
            ConsoleStyle.visibleLength(headers[column]),
            shown.maxOfOrNull { ConsoleStyle.visibleLength(it[column]) } ?: 0
        )
    }

    fun rule(left: String, middle: String, right: String) =
        dim(left + widths.joinToString(middle) { g.horizontal.repeat(it + 2) } + right)

    fun line(cells: List<String>) =
        dim(g.vertical) + cells.mapIndexed { column, cell ->
            " " + padVisible(cell, widths[column], alignRight = column in rightAligned) + " "
        }.joinToString(dim(g.vertical)) + dim(g.vertical)

    return buildList {
        add(rule(g.topLeft, g.teeDown, g.topRight))
        add(line(headers.map { bold(cyan(it)) }))
        add(rule(g.teeRight, g.cross, g.teeLeft))
        shown.forEach { row ->
            add(line(row.mapIndexed { column, cell -> cellColors[column]?.invoke(cell) ?: cell }))
        }
        add(rule(g.bottomLeft, g.teeUp, g.bottomRight))
        if (hidden > 0) add(dim("  ${if (unicode) "…" else "..."} and $hidden more$moreHint"))
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
    return "$coloured " + padVisible(
        formatNumber(clamped, 0) + "%",
        PERCENT_WIDTH,
        alignRight = true
    )
}

private const val PERCENT_WIDTH = 4

fun formatNumber(value: Double, decimals: Int = 2): String =
    String.format(Locale.ROOT, "%.${decimals}f", value)


