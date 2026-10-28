package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.input.update.WarehouseUpdateInput
import org.bytebloom.logiroute.domain.model.result.WarehouseReport
import org.bytebloom.logiroute.presentation.presenter.WarehousesManagementPresenter
import org.bytebloom.logiroute.presentation.screens.WarehousesManagementScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.MenuAction
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.askDouble
import org.bytebloom.logiroute.ui.terminal.askText
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.confirm
import org.bytebloom.logiroute.ui.terminal.runActionMenu
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.percentBar
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

/** The terminal implementation of [WarehousesManagementScreen]. */
class WarehousesManagementTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (WarehousesManagementScreen) -> WarehousesManagementPresenter
) : TerminalScreenBase(terminal, style, uiScope), WarehousesManagementScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()
    private var showAllOnNextList = false

    override suspend fun show() {
        heading("Warehouses")
        loadingMessage = "Loading warehouses…"
        presenter.onViewReady().join()

        terminal.runActionMenu(
            style,
            "Warehouses — what do you want to do?",
            listOf(
                MenuAction("List all warehouses", setOf("list")) { listAll() },
                MenuAction("Find one by id", setOf("find")) { find() },
                MenuAction("Create a warehouse", setOf("create", "new")) { create() },
                MenuAction("Update a warehouse", setOf("update", "edit")) { update() },
                MenuAction("Delete a warehouse", setOf("delete")) { delete() },
                MenuAction("Warehouse report", setOf("report")) { report() },
                MenuAction("Load factor of a warehouse", setOf("load")) { loadFactor() },
                MenuAction("Hub lineage of a warehouse", setOf("lineage", "hub")) { lineage() }
            )
        )
    }

    private suspend fun listAll() {
        showAllOnNextList = true
        loadingMessage = "Loading warehouses…"
        presenter.onViewReady().join()
    }

    private suspend fun find() {
        val chosen = pick("Warehouse to show") ?: return
        loadingMessage = "Looking for ${chosen.id}…"
        presenter.onFindById(chosen.id).join()
    }

    private suspend fun create() {
        val name = terminal.askText("Name (Enter to cancel): ") ?: return
        val zone = terminal.askText("Regional zone (Enter to cancel): ") ?: return
        val latitude = terminal.askDouble("Latitude  (-90 to 90, Enter to cancel): ", style) ?: return
        val longitude = terminal.askDouble("Longitude (-180 to 180, Enter to cancel): ", style) ?: return

        loadingMessage = "Creating the warehouse…"
        presenter.onCreate(name, zone, longitude, latitude).join()
    }

    private suspend fun update() {
        val chosen = pick("Warehouse to update") ?: return
        terminal.writeLine(style.dim("  Press Enter to keep the current value."))
        val name = terminal.askText("Name [${chosen.name}]: ")
        val zone = terminal.askText("Regional zone [${chosen.regionalZone}]: ")
        val latitude = terminal.askDouble("Latitude [${chosen.latitude}]: ", style)
        val longitude = terminal.askDouble("Longitude [${chosen.longitude}]: ", style)

        loadingMessage = "Updating ${chosen.id}…"
        presenter.onUpdate(
            WarehouseUpdateInput(
                chosen.id,
                name = name,
                regionalZone = zone,
                longitude = longitude,
                latitude = latitude
            )
        ).join()
    }

    private suspend fun delete() {
        val chosen = pick("Warehouse to delete") ?: return
        if (!terminal.confirm("Delete ${chosen.name} (${chosen.id})?")) return

        loadingMessage = "Deleting ${chosen.id}…"
        presenter.onDelete(chosen.id).join()
    }

    private suspend fun report() {
        val chosen = pick("Warehouse for the report") ?: return
        loadingMessage = "Building the report…"
        presenter.onReport(chosen.id).join()
    }

    private suspend fun loadFactor() {
        val chosen = pick("Warehouse to measure") ?: return
        loadingMessage = "Calculating the load factor…"
        presenter.onLoadFactor(chosen.id).join()
    }

    private suspend fun lineage() {
        val chosen = pick("Warehouse to trace") ?: return
        loadingMessage = "Tracing the hub lineage…"
        presenter.onHubLineage(chosen.id).join()
    }

    private suspend fun pick(label: String): Warehouse? {
        if (warehouses.isEmpty()) {
            printWarning("There are no warehouses yet.")
            return null
        }
        printLines(style.warehouseTable(warehouses, numbered = true))
        return terminal.chooseFrom("$label (index or id, Enter to cancel): ",
            warehouses, Warehouse::id, style)
    }

    override fun showWarehouses(warehouses: List<Warehouse>) {
        this.warehouses = warehouses
        terminal.writeLine()

        if (warehouses.isEmpty()) {
            printWarning("There are no warehouses yet.")
            return
        }

        val showAll = showAllOnNextList
        showAllOnNextList = false

        printLines(
            if (showAll) {
                style.warehouseTable(warehouses, limit = null)
            } else {
                style.warehouseTable(warehouses)
            }
        )
    }

    override fun showWarehouse(warehouse: Warehouse) {
        printLines(
            style.box(
                title = warehouse.name,
                lines = listOf(
                    "Id          ${warehouse.id}",
                    "Zone        ${warehouse.regionalZone}",
                    "Position    ${formatNumber(warehouse.latitude, COORDINATE_DECIMALS)}," +
                            " ${formatNumber(warehouse.longitude, COORDINATE_DECIMALS)}",
                    "Cargo queue ${warehouse.cargoQueue.size} package(s)",
                    "Vehicles    ${warehouse.stationedVehicles.size} stationed",
                    "Roads out   ${warehouse.outgoingRoutes.size}"
                )
            )
        )
    }

    override fun showWarehouseNotFound(id: String) = printWarning("Warehouse '$id' was not found.")

    override fun showWarehouseCreated(warehouse: Warehouse) {
        warehouses = warehouses + warehouse
        printSuccess("Created ${warehouse.name} (${warehouse.id}).")
    }

    override fun showWarehouseUpdated(warehouse: Warehouse) {
        warehouses = warehouses.map { if (it.id == warehouse.id) warehouse else it }
        printSuccess("Updated ${warehouse.name} (${warehouse.id}).")
    }

    override fun showWarehouseDeleted(id: String, wasDeleted: Boolean) {
        if (wasDeleted) {
            warehouses = warehouses.filterNot { it.id == id }
            printSuccess("Deleted $id.")
        } else {
            printWarning("Nothing was deleted: $id does not exist.")
        }
    }

    override fun showReport(report: WarehouseReport) {
        printLines(
            style.box(
                title = "Report of ${report.warehouseId}",
                lines = listOf(
                    "Packages waiting   ${report.packageCount}",
                    "Total weight       ${formatNumber(report.totalPackageWeight, 1)} kg",
                    "Vehicle capacity   ${formatNumber(report.totalVehicleCapacity, 1)} kg"
                )
            )
        )
    }

    override fun showLoadFactor(warehouse: Warehouse, loadFactor: Double) {
        terminal.writeLine(
            "${style.bold(warehouse.name)}  load " + style.percentBar(loadFactor * PERCENT) +
                    style.dim("  (cargo weight / vehicle capacity = ${formatNumber(loadFactor)})")
        )
    }

    override fun showHubLineage(warehouse: Warehouse, lineage: List<Warehouse>) {
        if (lineage.isEmpty()) {
            printWarning("${warehouse.name} is not part of the hub tree.")
            return
        }
        terminal.writeLine(lineage.joinToString(" ${style.glyphs.arrow} ") { it.name })
        terminal.writeLine(style.dim("  (the warehouse first, the global hub last)"))
    }

    private companion object {
        const val COORDINATE_DECIMALS = 4
        const val PERCENT = 100.0
    }
}
