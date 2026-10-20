package org.bytebloom.logiroute.ui.terminal.widget

import org.bytebloom.logiroute.ui.terminal.ConsoleStyle

private const val ERROR_TITLE = "Something went wrong"

fun ConsoleStyle.errorBox(message: String): List<String> =
    box(title = red("${glyphs.warning} $ERROR_TITLE"), lines = listOf(message))

fun ConsoleStyle.hint(text: String): String = dim(text)
