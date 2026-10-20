package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.presentation.presenter.PackagesManagementPresenter
import org.bytebloom.logiroute.presentation.screens.PackagesManagementScreen
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
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

/** The terminal implementation of [PackagesManagementScreen]. */
class PackagesManagementTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (PackagesManagementScreen) -> PackagesManagementPresenter
) : TerminalScreenBase(terminal, style, uiScope), PackagesManagementScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var packages: List<Package> = emptyList()

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    override suspend fun show() {
        heading("Packages")
        loadingMessage = "Loading packages…"
        presenter.onViewReady().join()

        terminal.runActionMenu(
            style,
            "Packages — what do you want to do?",
            listOf(
                MenuAction("List all packages", setOf("list")) { listAll() },
                MenuAction("Find one by id", setOf("find")) { find() },
                MenuAction("Create a package", setOf("create", "new")) { create() },
                MenuAction("Update a package", setOf("update", "edit")) { update() },
                MenuAction("Delete a package", setOf("delete")) { delete() },
                MenuAction("Packages heavier than a weight",
                    setOf("weight")) { aboveWeight() },
                MenuAction("Packages going to a warehouse",
                    setOf("destination")) { byDestination() },
                MenuAction("Packages with a priority", setOf("priority")) { byPriority() }
            )
        )
    }

    private suspend fun listAll() {
        loadingMessage = "Loading packages…"
        presenter.onViewReady().join()
    }

    private suspend fun find() {
        val chosen = pickPackage("Package to show") ?: return
        loadingMessage = "Looking for ${chosen.id}…"
        presenter.onFindById(chosen.id).join()
    }

    private suspend fun create() {
        val weight = terminal.askDouble("Weight in kg (Enter to cancel): ", style) ?: return
        val priority = pickPriority("Priority") ?: return
        val origin = pickWarehouse("Origin warehouse") ?: return
        val destination = pickWarehouse("Destination warehouse") ?: return

        loadingMessage = "Creating the package…"
        presenter.onCreate(
            weight,
            priority,
            origin.id,
            destination.id
        ).join()
    }

    private suspend fun update() {
        val chosen = pickPackage("Package to update") ?: return
        terminal.writeLine(style.dim("  Press Enter to keep the current value."))
        val weight = terminal.askDouble("Weight in kg [${chosen.weight}]: ", style)
        val priority = if (terminal.confirm("Change the priority" +
                    " (now ${chosen.priority})?")) pickPriority("New priority") else null
        val origin = if (terminal.confirm("Change the origin" +
                    " (now ${chosen.originWarehouse.name})?")) pickWarehouse("New origin") else null
        val destination = if (terminal.confirm("Change the destination " +
                    "(now ${chosen.destinationWarehouse.name})?")) {
            pickWarehouse("New destination")
        } else {
            null
        }

        loadingMessage = "Updating ${chosen.id}…"
        presenter.onUpdate(
            chosen.id,
            weight,
            priority,
            origin?.id,
            destination?.id
        ).join()
    }

    private suspend fun delete() {
        val chosen = pickPackage("Package to delete") ?: return
        if (!terminal.confirm("Delete ${chosen.id}?")) return

        loadingMessage = "Deleting ${chosen.id}…"
        presenter.onDelete(chosen.id).join()
    }

    private suspend fun aboveWeight() {
        val weight = terminal.askDouble("Minimum weight in kg (Enter to cancel): ", style) ?: return
        loadingMessage = "Searching…"
        presenter.onFindAboveWeight(weight).join()
    }

    private suspend fun byDestination() {
        val destination = pickWarehouse("Destination") ?: return
        loadingMessage = "Searching…"
        presenter.onFindByDestination(destination.id).join()
    }

    private suspend fun byPriority() {
        val priority = pickPriority("Priority") ?: return
        loadingMessage = "Searching…"
        presenter.onFindByPriority(priority).join()
    }

    private suspend fun pickPackage(label: String): Package? {
        if (packages.isEmpty()) {
            printWarning("There are no packages yet.")
            return null
        }
        printLines(style.packageTable(packages, numbered = true))
        return terminal.chooseFrom("$label (index or id, Enter to cancel): ",
            packages, Package::id, style)
    }

    private suspend fun pickWarehouse(label: String): Warehouse? {
        if (warehouses.isEmpty()) {
            printWarning("There are no warehouses yet.")
            return null
        }
        printLines(style.warehouseTable(warehouses, numbered = true))
        return terminal.chooseFrom("$label (index or id, Enter to cancel): ",
            warehouses, Warehouse::id, style)
    }

    private suspend fun pickPriority(label: String): Priority? {
        val priorities = Priority.entries
        priorities.forEachIndexed { index, priority -> terminal.writeLine("" +
                "  ${style.cyan((index + 1).toString())}  ${priority.name}") }
        return terminal.chooseFrom("$label (index or name, Enter to cancel): ",
            priorities, Priority::name, style)
    }

    override fun showPackages(packages: List<Package>) {
        this.packages = packages
        terminal.writeLine()
        if (packages.isEmpty())
            printWarning("There are no packages yet.")
        else
            printLines(style.packageTable(packages))
    }

    override fun showWarehouseChoices(warehouses: List<Warehouse>) {
        this.warehouses = warehouses
    }

    override fun showPackage(pkg: Package) {
        printLines(
            style.box(
                title = pkg.id,
                lines = listOf(
                    "Weight       ${formatNumber(pkg.weight, 1)} kg",
                    "Priority     ${pkg.priority.name}",
                    "From         ${pkg.originWarehouse.name} (${pkg.originWarehouse.id})",
                    "To           ${pkg.destinationWarehouse.name} (${pkg.destinationWarehouse.id})"
                )
            )
        )
    }

    override fun showPackageNotFound(id: String) = printWarning("Package '$id' was not found.")

    override fun showPackageCreated(pkg: Package) {
        packages = packages + pkg
        printSuccess("Created ${pkg.id}:" +
                " ${pkg.originWarehouse.name} ${style.glyphs.arrow} ${pkg.destinationWarehouse.name}.")
    }

    override fun showPackageUpdated(pkg: Package) {
        packages = packages.map { if (it.id == pkg.id) pkg else it }
        printSuccess("Updated ${pkg.id}.")
    }

    override fun showPackageDeleted(id: String, wasDeleted: Boolean) {
        if (wasDeleted) {
            packages = packages.filterNot { it.id == id }
            printSuccess("Deleted $id.")
        } else {
            printWarning("Nothing was deleted: $id does not exist.")
        }
    }

    override fun showMatchingPackages(criteria: String, packages: List<Package>) {
        terminal.writeLine()
        if (packages.isEmpty()) {
            printWarning("No package $criteria.")
            return
        }
        terminal.writeLine(style.bold("${packages.size} package(s) $criteria"))
        printLines(style.packageTable(packages))
    }
}
