package org.bytebloom.logiroute.domain.dispatch

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle

abstract class BaseDispatchProcessor(
    private val shipmentStateUpdater: ShipmentStateUpdater
) {

    fun dispatch(order: DispatchOrder): DispatchOutcome {
        val rejectionReason = validateCargo(order)
        val outcome = if (rejectionReason == null) {
            reserveVehicleCapacity(order)

            try {
                updateShipmentState(order)
                DispatchOutcome.Dispatched(order)
            } catch (exception: Exception) {
                restoreVehicleCapacity(order)
                throw exception
            }
        } else {
            DispatchOutcome.Rejected(order, rejectionReason)
        }
        notifyDispatchStatus(outcome)
        return outcome
    }

    private fun restoreVehicleCapacity(order: DispatchOrder) {
        order.packages.forEach(order.warehouse::addPackage)
        order.warehouse.addVehicle(order.vehicle)
    }

    private fun validateCargo(order: DispatchOrder): DispatchRejectionReason? = when {
        order.packages.isEmpty() -> DispatchRejectionReason.NO_PACKAGES
        !order.packages.all(order.warehouse::containsPackage) -> DispatchRejectionReason.PACKAGE_NOT_IN_QUEUE
        !order.warehouse.hasVehicle(order.vehicle) -> DispatchRejectionReason.VEHICLE_NOT_STATIONED
        !order.packages.all(::isPriorityAllowed) -> DispatchRejectionReason.PRIORITY_NOT_ALLOWED
        order.totalWeightKg > maxLoadKg(order.vehicle) -> DispatchRejectionReason.CAPACITY_EXCEEDED
        else -> null
    }

    private fun reserveVehicleCapacity(order: DispatchOrder) {
        order.packages.forEach(order.warehouse::removePackage)
        order.warehouse.removeVehicle(order.vehicle)
    }

    private fun updateShipmentState(order: DispatchOrder) {
        shipmentStateUpdater.markAsDispatched(order)
    }

    protected abstract fun isPriorityAllowed(pkg: Package): Boolean

    protected abstract fun maxLoadKg(vehicle: Vehicle): Double

    protected open fun notifyDispatchStatus(outcome: DispatchOutcome) = Unit
}