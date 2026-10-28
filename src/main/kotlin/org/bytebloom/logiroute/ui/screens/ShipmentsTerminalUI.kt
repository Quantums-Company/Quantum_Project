package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.input.ShipmentTransitionAction
import org.bytebloom.logiroute.domain.shipment.Shipment
import org.bytebloom.logiroute.presentation.presenter.ShipmentsPresenter
import org.bytebloom.logiroute.presentation.screens.ShipmentsScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.MenuAction
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.runActionMenu
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.packageTable
import org.bytebloom.logiroute.ui.terminal.widget.warehouseTable

/** The terminal implementation of [ShipmentsScreen]. */
class ShipmentsTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (ShipmentsScreen) -> ShipmentsPresenter
) : TerminalScreenBase(terminal, style, uiScope), ShipmentsScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var packages: List<Package> = emptyList()

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    override suspend fun show() {
        heading("Shipments")
        loadingMessage = "Loading packages and warehouses…"
        presenter.onViewReady().join()

        terminal.runActionMenu(
            style,
            "Shipments — what do you want to do?",
            listOf(
                MenuAction("Show a shipment's state", setOf("track"))
                { onPackage { presenter.onTrack(it.id).join() } },
                MenuAction("Move a shipment to its next step",
                    setOf("advance")) { advance() },
                MenuAction("Send a shipment to another destination",
                    setOf("reroute")) { reroute() },
                MenuAction("Estimate the delivery time",
                    setOf("estimate")) {
                    loadingMessage = "Estimating…"
                    onPackage { presenter.onEstimateDelivery(it.id).join() }
                }
            )
        )
    }

    private suspend fun onPackage(action: suspend (Package) -> Unit) {
        if (packages.isEmpty()) return printWarning("There are no packages yet.")
        printLines(style.packageTable(packages, numbered = true))
        val pkg = terminal.chooseFrom("Package (index or id, Enter to go back): ",
            packages, Package::id, style) ?: return
        action(pkg)
    }

    private suspend fun advance() = onPackage { pkg ->
        val actions = ShipmentTransitionAction.entries
        actions.forEachIndexed { index, action ->
            terminal.writeLine("  ${style.cyan((index + 1).toString())}  ${describe(action)}") }
        val action = terminal.chooseFrom("Step (index, Enter to go back): ",
            actions, ShipmentTransitionAction::name, style)
            ?: return@onPackage
        presenter.onAdvance(pkg.id, action).join()
    }

    private suspend fun reroute() = onPackage { pkg ->
        printLines(style.warehouseTable(warehouses, numbered = true))
        val destination = terminal.chooseFrom("New destination (index or id): ",
            warehouses, Warehouse::id, style)
            ?: return@onPackage
        presenter.onReroute(pkg.id, destination.id).join()
    }

    private fun describe(action: ShipmentTransitionAction): String = when (action) {
        ShipmentTransitionAction.ASSIGN_TO_VEHICLE -> "Assign to a vehicle"
        ShipmentTransitionAction.START_TRANSIT -> "Start transit"
        ShipmentTransitionAction.MARK_DELIVERED -> "Mark delivered"
        ShipmentTransitionAction.MARK_DELIVERY_FAILED -> "Mark delivery failed"
    }

    override fun showShipmentData(packages: List<Package>, warehouses: List<Warehouse>) {
        this.packages = packages
        this.warehouses = warehouses
    }

    override fun showShipment(shipment: Shipment) =
        terminal.writeLine("  ${shipment.cargo.id}: ${style.bold(shipment.state.name)} " +
                "(${shipment.cargo.originWarehouse.name}" +
                " ${style.glyphs.arrow} ${shipment.cargo.destinationWarehouse.name})")

    override fun showShipmentAdvanced(shipment: Shipment, action: ShipmentTransitionAction) =
        printSuccess("${shipment.cargo.id} is now ${shipment.state.name}.")

    override fun showShipmentRerouted(shipment: Shipment) =
        printSuccess("${shipment.cargo.id} now goes to " +
                "${shipment.cargo.destinationWarehouse.name} (kept in this session only).")

    override fun showDeliveryEstimate(packageId: String, totalDelayMinutes: Double?) {
        if (totalDelayMinutes == null) {
            printWarning("No complete route exists for" +
                    " $packageId, so there is no estimate.")
        } else {
            printSuccess("$packageId: about" +
                    " ${formatNumber(totalDelayMinutes, 0)}" +
                    " minutes of typical delay along the route.")
        }
    }
}
