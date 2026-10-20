package org.bytebloom.logiroute.ui.terminal

suspend fun Terminal.prompt(label: String): String? {
    write(label)
    return readLine()?.trim()
}

fun Terminal.writeLines(lines: List<String>) = lines.forEach(::writeLine)