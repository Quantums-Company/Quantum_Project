package org.bytebloom.logiroute.presentation.ui.console.widget

import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.ui.console.ConsoleStyle

fun ConsoleStyle.errorBox(error: UiError): List<String> {
    val headline = when (error) {
        is UiError.Network -> "No connection"
        is UiError.NotFound -> "Not found"
        is UiError.Conflict -> "Conflict"
        is UiError.InvalidInput -> "Invalid input"
        is UiError.Unexpected -> "Something went wrong"
    }
    return box(
        title = red("${glyphs.warning} $headline"),
        lines = listOf(error.detail)
    )
}

fun ConsoleStyle.hint(text: String): String = dim(text)
