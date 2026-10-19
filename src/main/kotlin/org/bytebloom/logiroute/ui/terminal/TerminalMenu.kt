package org.bytebloom.logiroute.ui.terminal

import org.bytebloom.logiroute.ui.screen.ClassificationTerminalUI
import org.bytebloom.logiroute.ui.screen.ShortestRouteTerminalUI
import org.bytebloom.logiroute.ui.screen.TelemetryTerminalUI
import org.koin.core.Koin

/**
 * Builds the menu of the terminal application.
 *
 * @param showAllScreens `false` shows only the screens required by the assignment (Task 5);
 *                       `true` adds every other screen of the application.
 */
fun buildTerminalMenu(koin: Koin, showAllScreens: Boolean): List<MenuSection> {
    val taskFive = MenuSection(
        title = null,
        entries = listOf(
            MenuEntry("Plan the shortest route", setOf("route", "shortest"), koin.get<ShortestRouteTerminalUI>()),
            MenuEntry("Classify a package (KNN)", setOf("classify", "knn"), koin.get<ClassificationTerminalUI>()),
            MenuEntry("Live vehicle telemetry", setOf("telemetry", "live"), koin.get<TelemetryTerminalUI>())
        )
    )
    return listOf(taskFive)
}
