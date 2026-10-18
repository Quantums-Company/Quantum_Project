package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.domain.command.AssignPackageToQueueCommand
import org.bytebloom.logiroute.domain.command.CommandInvoker
import org.bytebloom.logiroute.domain.command.DispatchVehicleCommand
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.usecase.dispatch.AssignPackageToCargoQueueUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.screen.CommandPatternScreen

/** Demonstrates Task 5 using the existing Command and CommandInvoker implementations. */
class CommandPatternPresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllPackages: GetAllPackagesUseCase,
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val assignPackage: AssignPackageToCargoQueueUseCase,
    private val dispatchVehicle: DispatchVehicleUseCase,
    private val screen: CommandPatternScreen
) {
    suspend fun runDemo() {
        screen.showTitle()
        screen.showLoadingIndicator(true)
        try {
            val warehouses = getAllWarehouses()
            val packages = getAllPackages()
            val vehicles = getAllVehicles()
            val compatible = findCompatiblePair(warehouses, packages, vehicles)
            if (compatible == null) {
                screen.showErrorMessage(
                    "No package/vehicle pair shares an origin warehouse with enough capacity. " +
                        "Check the CSV data before presenting Task 5."
                )
                return
            }

            val (warehouse, pkg, vehicle) = compatible
            // Use an isolated in-memory warehouse so the demo cannot mutate repository snapshots.
            warehouse.addVehicle(vehicle)
            val invoker = CommandInvoker()
            val assignCommand = AssignPackageToQueueCommand(warehouse, pkg, assignPackage)
            val dispatchCommand = DispatchVehicleCommand(
                dispatchVehicleUseCase = dispatchVehicle,
                packages = listOf(pkg),
                vehicle = vehicle,
                warehouse = warehouse
            )

            screen.showStep("Initial state", true, "Warehouse=${warehouse.id}, package=${pkg.id}, vehicle=${vehicle.id}")
            showState("Initial", warehouse, invoker)

            val assigned = invoker.execute(assignCommand)
            screen.showStep("Execute AssignPackageToQueueCommand", assigned,
                if (assigned) "Package ${pkg.id} added to the cargo queue." else "Assignment was rejected.")
            showState("After assignment", warehouse, invoker)
            if (!assigned) return

            val dispatched = invoker.execute(dispatchCommand)
            screen.showStep("Execute DispatchVehicleCommand", dispatched,
                if (dispatched) "Package dispatched and vehicle released from the warehouse." else "Dispatch was rejected.")
            showState("After dispatch", warehouse, invoker)
            if (!dispatched) return

            val undoDispatch = invoker.undo()
            screen.showStep("Undo #1 (dispatch)", undoDispatch, "The package and vehicle should be restored.")
            showState("After undo #1", warehouse, invoker)

            val undoAssignment = invoker.undo()
            screen.showStep("Undo #2 (assignment)", undoAssignment, "The package should be removed from the queue.")
            showState("After undo #2", warehouse, invoker)

            val redoAssignment = invoker.redo()
            screen.showStep("Redo #1 (assignment)", redoAssignment, "The package is assigned again.")
            showState("After redo #1", warehouse, invoker)

            val redoDispatch = invoker.redo()
            screen.showStep("Redo #2 (dispatch)", redoDispatch, "The dispatch is replayed.")
            showState("After redo #2", warehouse, invoker)

            // Leave the isolated demo state clean after the demonstration.
            invoker.undo()
            invoker.undo()
            warehouse.removeVehicle(vehicle)
            screen.showFinished("Task 5 demo completed. Undo/redo history was demonstrated and demo state was cleaned up.")
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }

    private fun findCompatiblePair(
        warehouses: List<Warehouse>,
        packages: List<Package>,
        vehicles: List<Vehicle>
    ): Triple<Warehouse, Package, Vehicle>? {
        for (pkg in packages) {
            val vehicle = vehicles.firstOrNull {
                it.currentWarehouse.id == pkg.originWarehouse.id && it.canCarryWeight(pkg.weight)
            } ?: continue
            val source = warehouses.firstOrNull { it.id == pkg.originWarehouse.id } ?: pkg.originWarehouse
            val isolated = Warehouse(
                id = source.id,
                name = source.name,
                regionalZone = source.regionalZone,
                longitude = source.longitude,
                latitude = source.latitude
            )
            return Triple(isolated, pkg, vehicle)
        }
        return null
    }

    private fun showState(label: String, warehouse: Warehouse, invoker: CommandInvoker) {
        screen.showState(
            label = label,
            queuedPackageIds = warehouse.cargoQueue.map { it.id },
            stationedVehicleIds = warehouse.stationedVehicles.map { it.id },
            canUndo = invoker.canUndo(),
            canRedo = invoker.canRedo()
        )
    }
}
