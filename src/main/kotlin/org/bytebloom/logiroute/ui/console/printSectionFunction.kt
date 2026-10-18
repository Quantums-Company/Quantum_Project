package org.bytebloom.logiroute.ui.console

fun printSection(title: String) {
    println()
    println("╔══════════════════════════════════════════════════════════════╗")
    println("║ ${title.padEnd(60).take(60)} ║")
    println("╚══════════════════════════════════════════════════════════════╝")
}

