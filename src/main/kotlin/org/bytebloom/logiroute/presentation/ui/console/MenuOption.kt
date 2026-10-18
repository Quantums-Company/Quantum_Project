package org.bytebloom.logiroute.presentation.ui.console

enum class MenuOption(val key: String, val label: String, private val aliases: Set<String>) {
    OVERVIEW("1", "System overview", setOf("overview", "dashboard")),
    ROUTE("2", "Plan the shortest route", setOf("route")),
    CLASSIFY("3", "Classify a package (KNN)", setOf("classify", "knn")),
    TELEMETRY("4", "Live fleet telemetry", setOf("telemetry", "live")),
    EXIT("0", "Exit", setOf("exit", "quit", "q"));

    companion object {
        fun parse(input: String): MenuOption? {
            val normalized = input.trim().lowercase()
            return entries.firstOrNull { it.key == normalized || normalized in it.aliases }
        }
    }
}
