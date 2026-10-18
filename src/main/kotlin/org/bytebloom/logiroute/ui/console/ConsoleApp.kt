package org.bytebloom.logiroute.ui.console

import org.bytebloom.logiroute.presentation.ClassificationPresenter
import org.bytebloom.logiroute.presentation.CommandPatternPresenter
import org.bytebloom.logiroute.presentation.DashboardPresenter
import org.bytebloom.logiroute.presentation.PackagesManagementPresenter
import org.bytebloom.logiroute.presentation.RoutesManagementPresenter
import org.bytebloom.logiroute.presentation.TelemetryPresenter
import org.bytebloom.logiroute.presentation.VehiclesManagementPresenter

/** Console is only one implementation of the screen contracts; Android can replace it later. */
class ConsoleApp(
    private val commandPatternPresenter: CommandPatternPresenter,
    private val dashboardPresenter: DashboardPresenter,
    private val packagesPresenter: PackagesManagementPresenter,
    private val vehiclesPresenter: VehiclesManagementPresenter,
    private val routesPresenter: RoutesManagementPresenter,
    private val classificationPresenter: ClassificationPresenter,
    private val telemetryPresenter: TelemetryPresenter
) {
    suspend fun run() {
        // Task 5 is the default demonstration. Other screens are opt-in for the presentation.
        commandPatternPresenter.runDemo()
        println()
        print("Task 5 presentation finished. Would you like to explore all screens now? [y/N]: ")
        if (readlnOrNull()?.trim()?.equals("y", ignoreCase = true) != true) {
            println("LogiRoute Task 5 demo finished.")
            return
        }

        while (true) {
            printMenu()
            when (readlnOrNull()?.trim()?.lowercase()) {
                "1", "dashboard" -> dashboardPresenter.showDashboard()
                "2", "packages" -> showPackages()
                "3", "vehicles" -> vehiclesPresenter.loadVehicles()
                "4", "routes" -> showRoutes()
                "5", "classify" -> showClassification()
                "6", "telemetry" -> telemetryPresenter.showSampleTelemetry()
                "7", "task5" -> commandPatternPresenter.runDemo()
                "0", "exit", null -> break
                else -> println("Invalid choice. Select 0-7.")
            }
        }
        println("Goodbye from LogiRoute.")
    }

    private suspend fun showPackages() {
        packagesPresenter.loadPackages()
        print("Package ID for details (Enter to return): ")
        readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }?.let(packagesPresenter::showPackage)
    }

    private suspend fun showRoutes() {
        routesPresenter.loadWarehouses()
        print("Origin warehouse ID (Enter to return): ")
        val origin = readlnOrNull()?.trim().orEmpty()
        if (origin.isEmpty()) return
        print("Destination warehouse ID: ")
        val destination = readlnOrNull()?.trim().orEmpty()
        if (destination.isNotEmpty()) routesPresenter.calculateRoute(origin, destination)
    }

    private suspend fun showClassification() {
        classificationPresenter.loadPackages()
        print("Package ID to classify (Enter to return): ")
        readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }?.let { classificationPresenter.classify(it) }
    }

    private fun printMenu() {
        println("""

        ╔══════════════════════════════════════════════╗
        ║                 LOGIROUTE                    ║
        ╠══════════════════════════════════════════════╣
        ║ 1  Dashboard                                  ║
        ║ 2  Package management                         ║
        ║ 3  Vehicle management                         ║
        ║ 4  Route planner                              ║
        ║ 5  Package classification                     ║
        ║ 6  Fleet telemetry                            ║
        ║ 7  Task 5 — Command Pattern Dispatch Panel     ║
        ║ 0  Exit                                       ║
        ╚══════════════════════════════════════════════╝
        """.trimIndent())
        print("Choose an option: ")
    }
}
