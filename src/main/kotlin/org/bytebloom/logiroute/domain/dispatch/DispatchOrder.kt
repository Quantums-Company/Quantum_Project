package org.bytebloom.logiroute.domain.dispatch

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse

data class DispatchOrder(
    val packages: List<Package>,
    val vehicle: Vehicle,
    val warehouse: Warehouse
) {
    val totalWeightKg: Double
        get() = packages.sumOf { it.weight }
}