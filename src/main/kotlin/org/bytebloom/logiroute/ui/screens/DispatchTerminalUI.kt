package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.dispatch.DispatchMode
import org.bytebloom.logiroute.domain.dispatch.DispatchOutcome
import org.bytebloom.logiroute.domain.dispatch.DispatchRejectionReason
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.result.KnapsackResult
import org.bytebloom.logiroute.presentation.presenter.DispatchPresenter
import org.bytebloom.logiroute.presentation.screens.DispatchScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.MenuAction
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.runActionMenu
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.packageTable
import org.bytebloom.logiroute.ui.terminal.widget.table
import org.bytebloom.logiroute.ui.terminal.widget.vehicleTable
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

private const val COL_ONE = 1
private const val COL_TWO = 2
private const val COL_THREE = 3

/** The terminal implementation of [DispatchScreen]. */
class DispatchTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (DispatchScreen) -> DispatchPresenter
) : TerminalScreenBase(terminal, style, uiScope), DispatchScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    @Volatile
    private var vehicles: List<Vehicle> = emptyList()

    @Volatile
    private var packages: List<Package> = emptyList()

    override suspend fun show() {
        heading("Dispatch")
        refresh()

        terminal.runActionMenu(
            style,
            "Dispatch — what do you want to do?",
            listOf(
                MenuAction("What waits in each warehouse",
                    setOf("status", "queues")) { status() },
                MenuAction("Put a package in its warehouse's cargo queue",
                    setOf("assign", "queue")) { assign() },
                MenuAction("Station a vehicle at a warehouse",
                    setOf("station")) { station() },
                MenuAction("Dispatch cargo (standard or express)",
                    setOf("dispatch")) { dispatchCargo() },
                MenuAction("Check if a vehicle can take a group of packages",
                    setOf("check")) { dispatchVehicle() },
                MenuAction("Best load for a vehicle (maximum priority within capacity)",
                    setOf("load", "knapsack")) { optimizeLoad() }
            )
        )
    }

    private suspend fun refresh() {
        loadingMessage = "Loading warehouses, vehicles and packages…"
        presenter.onViewReady().join()
    }

    private fun status() {
        if (warehouses.isEmpty()) {
            printWarning("There are no warehouses yet.")
            return
        }
        terminal.writeLine()
        printLines(
            style.table(
                headers = listOf("Warehouse", "Packages queued", "Queued weight (kg)", "Vehicles stationed"),
                rows = warehouses.map {
                    listOf(
                        "${it.name} (${it.id})",
                        it.cargoQueue.size.toString(),
                        formatNumber(it.cargoQueue.sumOf { pkg -> pkg.weight }, 1),
                        it.stationedVehicles.size.toString()
                    )
                },
                rightAligned = setOf(COL_ONE, COL_TWO, COL_THREE)
            )
        )
    }

    private suspend fun assign() {
        val warehouse = pickWarehouse("Warehouse") ?: return
        val pkg = pickFrom(
            "Package to queue",
            packages,
            Package::id
        ) { style.packageTable(it, numbered = true) } ?: return

        loadingMessage = "Queueing ${pkg.id}…"
        presenter.onAssignPackage(warehouse.id, pkg.id).join()
        refresh()
    }

    private suspend fun station() {
        val warehouse = pickWarehouse("Warehouse to station the vehicle at") ?: return
        val vehicle = pickFrom(
            "Vehicle",
            vehicles,
            Vehicle::id
        ) { style.vehicleTable(it, numbered = true) } ?: return

        loadingMessage = "Stationing ${vehicle.id}…"
        presenter.onAddVehicleToHub(warehouse.id, vehicle.id).join()
        refresh()
    }

    private suspend fun dispatchCargo() {
        val warehouse = pickWarehouse("Dispatch from") ?: return
        val vehicle = pickStationedVehicle(warehouse) ?: return
        val chosen = pickQueuedPackages(warehouse) ?: return
        val mode = pickFrom("Mode", DispatchMode.entries, DispatchMode::name) { list ->
            list.mapIndexed { index, mode -> "  ${style.cyan((index + 1).toString())}  ${mode.name}" }
        } ?: return

        loadingMessage = "Dispatching…"
        presenter.onDispatchCargo(
            warehouse.id,
            vehicle.id,
            chosen.map { it.id },
            mode
        ).join()
        refresh()
    }

    private suspend fun dispatchVehicle() {
        val warehouse = pickWarehouse("Warehouse") ?: return
        val vehicle = pickStationedVehicle(warehouse) ?: return
        val chosen = pickQueuedPackages(warehouse) ?: return

        loadingMessage = "Checking the dispatch…"
        presenter.onDispatchVehicle(
            warehouse.id,
            vehicle.id,
            chosen.map { it.id }
        ).join()
        refresh()
    }

    private suspend fun optimizeLoad() {
        val vehicle = pickFrom(
            "Vehicle",
            vehicles,
            Vehicle::id
        ) { style.vehicleTable(it, numbered = true) } ?: return
        val chosen = pickSeveral(
            "Add a candidate package",
            packages
        ) { style.packageTable(it, numbered = true) }
        if (chosen.isEmpty()) return

        loadingMessage = "Looking for the best load…"
        presenter.onOptimizeLoad(vehicle.id, chosen.map { it.id }).join()
    }

    private suspend fun pickWarehouse(label: String): Warehouse? =
        pickFrom(
            label,
            warehouses,
            Warehouse::id
        ) { style.warehouseTable(it, numbered = true) }

    private suspend fun pickStationedVehicle(warehouse: Warehouse): Vehicle? {
        if (warehouse.stationedVehicles.isEmpty()) {
            printWarning("No vehicle is stationed at ${warehouse.name}. Use \"Station a vehicle\" first.")
            return null
        }
        return pickFrom("Vehicle",
            warehouse.stationedVehicles,
            Vehicle::id) { style.vehicleTable(it, numbered = true) }
    }

    private suspend fun pickQueuedPackages(warehouse: Warehouse): List<Package>? {
        if (warehouse.cargoQueue.isEmpty()) {
            printWarning(
                "No package is queued at ${warehouse.name}. " +
                        "Use \"Put a package in its warehouse's cargo queue\" first."
            )
            return null
        }
        return pickSeveral("Add a package",
            warehouse.cargoQueue) { style.packageTable(it, numbered = true) }
            .takeIf { it.isNotEmpty() }
    }

    private suspend fun <T> pickFrom(
        label: String,
        items: List<T>,
        idOf: (T) -> String,
        render: (List<T>) -> List<String>
    ): T? {
        if (items.isEmpty()) {
            printWarning("There is nothing to choose from.")
            return null
        }
        printLines(render(items))
        return terminal.chooseFrom("$label (index or name, Enter to cancel): ", items, idOf, style)
    }

    private suspend fun pickSeveral(
        label: String,
        items: List<Package>,
        render: (List<Package>) -> List<String>
    ): List<Package> {
        if (items.isEmpty()) {
            printWarning("There is nothing to choose from.")
            return emptyList()
        }
        printLines(render(items))
        val chosen = mutableListOf<Package>()
        while (true) {
            val next = terminal.chooseFrom(
                "$label (index or id, Enter when finished): ",
                items.filterNot { it in chosen },
                Package::id,
                style
            ) ?: break
            chosen += next
        }
        return chosen
    }

    override fun showDispatchData(
        warehouses: List<Warehouse>,
        vehicles: List<Vehicle>,
        packages: List<Package>
    ) {
        this.warehouses = warehouses
        this.vehicles = vehicles
        this.packages = packages
    }

    override fun showPackageAssigned(pkg: Package, warehouse: Warehouse, wasAssigned: Boolean) {
        if (wasAssigned) {
            printSuccess("${pkg.id} now waits in the cargo queue of ${warehouse.name}.")
        } else {
            printWarning(
                pkg.id +
                        " was not queued: it must start at ${warehouse.name} and not be queued there already.")
        }
    }

    override fun showVehicleStationed(vehicle: Vehicle) =
        printSuccess("${vehicle.id} is now stationed at ${vehicle.currentWarehouse.name}.")

    override fun showVehicleDispatched(wasDispatched: Boolean, vehicle: Vehicle, packages: List<Package>) {
        if (wasDispatched) {
            printSuccess("${vehicle.id} can take ${packages.size} package(s) and the dispatch was done.")
        } else {
            printWarning(
                vehicle.id +
                        " cannot take these packages (not queued here, not stationed here, or too heavy).")
        }
    }

    override fun showDispatchOutcome(mode: DispatchMode, outcome: DispatchOutcome) {
        when (outcome) {
            is DispatchOutcome.Dispatched -> printLines(
                style.box(
                    title = style.green("${style.glyphs.success} Dispatched (${mode.name})"),
                    lines = listOf(
                        "Vehicle    ${outcome.order.vehicle.id}",
                        "From       ${outcome.order.warehouse.name}",
                        "Packages   ${outcome.order.packages.joinToString { it.id }}",
                        "Weight     ${formatNumber(outcome.order.totalWeightKg, 1)} kg"
                    )
                )
            )

            is DispatchOutcome.Rejected ->
                printWarning("Dispatch rejected: ${explain(outcome.reason, mode)}")
        }
    }

    override fun showLoadPlan(vehicle: Vehicle, candidates: List<Package>, result: KnapsackResult) {
        val chosenIds = result.selectedItems.map { it.id }
        printLines(
            style.box(
                title = style.green("${style.glyphs.success} Best load for ${vehicle.id}"),
                lines = listOf(
                    "Capacity   ${formatNumber(vehicle.maxCapacityKg, 1)} kg",
                    "Take       ${if (chosenIds.isEmpty()) "nothing fits" else chosenIds.joinToString()}",
                    "Weight     ${result.totalWeightKg} kg",
                    "Priority   ${result.totalPriorityValue} (sum of priority scores)",
                    "Left out   ${candidates.map { it.id }
                        .filterNot { it in chosenIds }.joinToString().ifEmpty { "none" }}"
                )
            )
        )
    }

    private fun explain(reason: DispatchRejectionReason, mode: DispatchMode): String = when (reason) {
        DispatchRejectionReason.NO_PACKAGES -> "no package was chosen."
        DispatchRejectionReason.PACKAGE_NOT_IN_QUEUE -> "a package is not in this warehouse's cargo queue."
        DispatchRejectionReason.VEHICLE_NOT_STATIONED -> "the vehicle is not stationed at this warehouse."
        DispatchRejectionReason.PRIORITY_NOT_ALLOWED -> "${mode.name} dispatch only accepts URGENT packages."
        DispatchRejectionReason.CAPACITY_EXCEEDED ->
            "the packages are heavier than the vehicle may carry in ${mode.name} mode."
    }
}