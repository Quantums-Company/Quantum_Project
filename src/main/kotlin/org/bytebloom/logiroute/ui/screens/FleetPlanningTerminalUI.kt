package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.result.BackhaulOpportunity
import org.bytebloom.logiroute.domain.model.result.CargoRecoveryPlan
import org.bytebloom.logiroute.domain.model.result.DispatchResult
import org.bytebloom.logiroute.presentation.presenter.FleetPlanningPresenter
import org.bytebloom.logiroute.presentation.screens.FleetPlanningScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.MenuAction
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.askText
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.runActionMenu
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.table
import org.bytebloom.logiroute.ui.terminal.widget.vehicleTable
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

/** The terminal implementation of [FleetPlanningScreen]. */
class FleetPlanningTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (FleetPlanningScreen) -> FleetPlanningPresenter
) : TerminalScreenBase(terminal, style, uiScope), FleetPlanningScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    @Volatile
    private var vehicles: List<Vehicle> = emptyList()

    override suspend fun show() {
        heading("Fleet planning")
        loadingMessage = "Loading warehouses and vehicles…"
        presenter.onViewReady().join()

        terminal.runActionMenu(
            style,
            "Fleet planning — what do you want to do?",
            listOf(
                MenuAction("Which vehicles cover these zones? (fewest vehicles)",
                    setOf("coverage")) { coverage() },
                MenuAction("Find a return load for a vehicle (backhaul)",
                    setOf("backhaul")) { backhaul() },
                MenuAction("A vehicle broke down: who takes its packages?",
                    setOf("recovery")) { recovery() }
            )
        )
    }

    private suspend fun coverage() {
        val zones = warehouses.map { it.regionalZone }.distinct()
        if (zones.isEmpty()) return printWarning("There are no warehouses yet.")
        terminal.writeLine("  Zones: ${zones.joinToString()}")
        val answer = terminal.askText("Zones to cover, separated by commas (Enter to go back): ") ?: return
        loadingMessage = "Choosing vehicles…"
        presenter.onPlanCoverage(answer.split(',').toSet()).join()
    }

    private suspend fun backhaul() {
        val vehicle = pickVehicle() ?: return
        printLines(style.warehouseTable(warehouses, numbered = true))
        val from = terminal.chooseFrom("Warehouse it delivers to (index or id): ",
            warehouses, Warehouse::id, style) ?: return
        loadingMessage = "Looking for a return load…"
        presenter.onFindBackhaul(vehicle.id, from.id).join()
    }

    private suspend fun recovery() {
        val vehicle = pickVehicle() ?: return
        loadingMessage = "Planning the recovery…"
        presenter.onFindRecovery(vehicle.id).join()
    }

    private suspend fun pickVehicle(): Vehicle? {
        if (vehicles.isEmpty()) {
            printWarning("There are no vehicles yet.")
            return null
        }
        printLines(style.vehicleTable(vehicles, numbered = true))
        return terminal.chooseFrom("Vehicle (index or id, Enter to go back): ",
            vehicles, Vehicle::id, style)
    }

    override fun showFleetData(warehouses: List<Warehouse>, vehicles: List<Vehicle>) {
        this.warehouses = warehouses
        this.vehicles = vehicles
    }

    override fun showCoveragePlan(targetZones: Set<String>, result: DispatchResult) {
        val title = if (result.isFullyCovered) {
            style.green("${style.glyphs.success} All zones covered")
        } else {
            style.yellow("${style.glyphs.warning} Some zones cannot be covered")
        }
        printLines(
            style.box(
                title,
                listOf(
                    "Vehicles   ${result.selectedVehicles.joinToString { it.id }.ifEmpty { "none" }}",
                    "Covered    ${result.coveredZones.joinToString().ifEmpty { "none" }}",
                    "Uncovered  ${result.uncoveredZones.joinToString().ifEmpty { "none" }}"
                )
            )
        )
    }

    override fun showBackhaulOpportunity(
        vehicle: Vehicle,
        returnFrom: Warehouse,
        opportunity: BackhaulOpportunity?
    ) {
        if (opportunity == null) {
            printWarning("No package waits at ${returnFrom.name} " +
                    "for ${vehicle.currentWarehouse.name}: ${vehicle.id} would return empty.")
            return
        }
        printSuccess("${vehicle.id} can bring ${opportunity.packages.size}" +
                " package(s) back from ${returnFrom.name}.")
        printLines(
            style.table(
                headers = listOf("Package", "Weight (kg)"),
                rows = opportunity.packages.map { listOf(it.id, formatNumber(it.weight, 1)) },
                rightAligned = setOf(1)
            )
        )
        terminal.writeLine("  " +
                "Load ${formatNumber(opportunity.totalCargoWeightKg, 1)} kg, " +
                "${formatNumber(opportunity.remainingCapacityKg, 1)} kg of capacity left.")
    }

    override fun showRecoveryPlan(failedVehicle: Vehicle, plan: CargoRecoveryPlan?) {
        if (plan == null) {
            printWarning("Nothing to recover: ${failedVehicle.currentWarehouse.name}" +
                    " has no queued packages or no other vehicle.")
            return
        }
        printSuccess("Packages of ${failedVehicle.id} are shared among the other vehicles:")
        printLines(
            style.table(
                headers = listOf("Package", "Rescue vehicle"),
                rows = plan.rescueVehicleByPackageId.map { (pkg, vehicle) -> listOf(pkg, vehicle) }
            )
        )
    }
}

