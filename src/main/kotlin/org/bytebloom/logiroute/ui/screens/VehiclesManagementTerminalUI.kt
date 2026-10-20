package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.presentation.presenter.VehiclesManagementPresenter
import org.bytebloom.logiroute.presentation.screens.VehiclesManagementScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.MenuAction
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.askDouble
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.confirm
import org.bytebloom.logiroute.ui.terminal.runActionMenu
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.packageTable
import org.bytebloom.logiroute.ui.terminal.widget.vehicleTable
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

/** The terminal implementation of [VehiclesManagementScreen]. */
class VehiclesManagementTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (VehiclesManagementScreen) -> VehiclesManagementPresenter
) : TerminalScreenBase(terminal, style, uiScope), VehiclesManagementScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var vehicles: List<Vehicle> = emptyList()

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    @Volatile
    private var packages: List<Package> = emptyList()

    override suspend fun show() {
        heading("Vehicles")
        loadingMessage = "Loading vehicles…"
        presenter.onViewReady().join()

        terminal.runActionMenu(
            style,
            "Vehicles — what do you want to do?",
            listOf(
                MenuAction("List all vehicles", setOf("list")) { listAll() },
                MenuAction("Find one by id", setOf("find")) { find() },
                MenuAction("Create a vehicle", setOf("create", "new")) { create() },
                MenuAction("Update a vehicle", setOf("update", "edit")) { update() },
                MenuAction("Delete a vehicle", setOf("delete")) { delete() },
                MenuAction("Vehicles stationed at a warehouse that can carry a load",
                    setOf("stationed")) { stationed() },
                MenuAction("Cheapest vehicle for a group of packages",
                    setOf("cheapest")) { cheapest() }
            )
        )
    }

    private suspend fun listAll() {
        loadingMessage = "Loading vehicles…"
        presenter.onViewReady().join()
    }

    private suspend fun find() {
        val chosen = pickVehicle("Vehicle to show") ?: return
        loadingMessage = "Looking for ${chosen.id}…"
        presenter.onFindById(chosen.id).join()
    }

    private suspend fun create() {
        val capacity = terminal.askDouble("Maximum capacity in kg (Enter to cancel): ", style) ?: return
        val cost = terminal.askDouble("Cost per km (Enter to cancel): ", style) ?: return
        val warehouse = pickWarehouse("Stationed at") ?: return

        loadingMessage = "Creating the vehicle…"
        presenter.onCreate(capacity, cost, warehouse.id).join()
    }

    private suspend fun update() {
        val chosen = pickVehicle("Vehicle to update") ?: return
        terminal.writeLine(style.dim("  Press Enter to keep the current value."))
        val capacity = terminal.askDouble("Maximum capacity in kg [${chosen.maxCapacityKg}]: ", style)
        val cost = terminal.askDouble("Cost per km [${chosen.costPerKm}]: ", style)
        val moveTo = if (terminal.confirm("Move it to another warehouse?"))
            pickWarehouse("New warehouse") else null

        loadingMessage = "Updating ${chosen.id}…"
        presenter.onUpdate(chosen.id, capacity, cost, moveTo?.id).join()
    }

    private suspend fun delete() {
        val chosen = pickVehicle("Vehicle to delete") ?: return
        if (!terminal.confirm("Delete ${chosen.id}?")) return

        loadingMessage = "Deleting ${chosen.id}…"
        presenter.onDelete(chosen.id).join()
    }

    private suspend fun stationed() {
        val warehouse = pickWarehouse("Warehouse") ?: return
        val capacity = terminal.askDouble(
            "Required capacity in kg (Enter to cancel): ",
            style
        ) ?: return

        loadingMessage = "Searching…"
        presenter.onFindStationed(warehouse.id, capacity).join()
    }

    private suspend fun cheapest() {
        if (packages.isEmpty()) {
            printWarning("There are no packages yet.")
            return
        }
        printLines(style.packageTable(packages, numbered = true))
        val chosen = mutableListOf<Package>()
        while (true) {
            val next = terminal.chooseFrom(
                "Add a package (index or id, Enter when finished): ",
                packages.filterNot { it in chosen },
                Package::id,
                style
            ) ?: break
            chosen += next
        }
        if (chosen.isEmpty()) return

        loadingMessage = "Looking for the cheapest vehicle…"
        presenter.onFindCheapest(chosen.map { it.id }).join()
    }

    private suspend fun pickVehicle(label: String): Vehicle? {
        if (vehicles.isEmpty()) {
            printWarning("There are no vehicles yet.")
            return null
        }
        printLines(style.vehicleTable(vehicles, numbered = true))
        return terminal.chooseFrom(
            "$label (index or id, Enter to cancel): ",
            vehicles,
            Vehicle::id,
            style
        )
    }

    private suspend fun pickWarehouse(label: String): Warehouse? {
        if (warehouses.isEmpty()) {
            printWarning("There are no warehouses yet.")
            return null
        }
        printLines(style.warehouseTable(warehouses, numbered = true))
        return terminal.chooseFrom(
            "$label (index or id, Enter to cancel): ",
            warehouses,
            Warehouse::id,
            style
        )
    }

    override fun showVehicles(vehicles: List<Vehicle>) {
        this.vehicles = vehicles
        terminal.writeLine()
        if (vehicles.isEmpty())
            printWarning("There are no vehicles yet.")
        else
            printLines(style.vehicleTable(vehicles))
    }

    override fun showWarehouseChoices(warehouses: List<Warehouse>) {
        this.warehouses = warehouses
    }

    override fun showPackageChoices(packages: List<Package>) {
        this.packages = packages
    }

    override fun showVehicle(vehicle: Vehicle) {
        printLines(
            style.box(
                title = vehicle.id,
                lines = listOf(
                    "Capacity      ${formatNumber(vehicle.maxCapacityKg, 1)} kg",
                    "Cost per km   ${formatNumber(vehicle.costPerKm)}",
                    "Stationed at  ${vehicle.currentWarehouse.name} (${vehicle.currentWarehouse.id})"
                )
            )
        )
    }

    override fun showVehicleNotFound(id: String) = printWarning("Vehicle '$id' was not found.")

    override fun showVehicleCreated(vehicle: Vehicle) {
        vehicles = vehicles + vehicle
        printSuccess("Created ${vehicle.id} at ${vehicle.currentWarehouse.name}.")
    }

    override fun showVehicleUpdated(vehicle: Vehicle) {
        vehicles = vehicles.map { if (it.id == vehicle.id) vehicle else it }
        printSuccess("Updated ${vehicle.id}.")
    }

    override fun showVehicleDeleted(id: String, wasDeleted: Boolean) {
        if (wasDeleted) {
            vehicles = vehicles.filterNot { it.id == id }
            printSuccess("Deleted $id.")
        } else {
            printWarning("Nothing was deleted: $id does not exist.")
        }
    }

    override fun showStationedVehicles(warehouse: Warehouse, requiredCapacityKg: Double, vehicles: List<Vehicle>) {
        terminal.writeLine()
        if (vehicles.isEmpty()) {
            printWarning("No vehicle stationed at ${warehouse.name}" +
                    " can carry ${formatNumber(requiredCapacityKg, 1)} kg.")
            return
        }
        terminal.writeLine(style.bold("Stationed at ${warehouse.name}," +
                " able to carry ${formatNumber(requiredCapacityKg, 1)} kg"))
        printLines(style.vehicleTable(vehicles))
    }

    override fun showCheapestVehicle(packages: List<Package>, vehicle: Vehicle?) {
        val weight = packages.sumOf { it.weight }
        if (vehicle == null) {
            printWarning("No vehicle can carry" +
                    " ${formatNumber(weight, 1)} kg in one trip.")
            return
        }
        printLines(
            style.box(
                title = style.green("${style.glyphs.success} Cheapest suitable vehicle"),
                lines = listOf(
                    "Vehicle   ${vehicle.id}" +
                            "  (${formatNumber(vehicle.maxCapacityKg, 1)} kg capacity)",
                    "Cost      ${formatNumber(vehicle.costPerKm)} per km",
                    "Load      ${packages.size} package(s), ${formatNumber(weight, 1)} kg"
                )
            )
        )
    }
}
