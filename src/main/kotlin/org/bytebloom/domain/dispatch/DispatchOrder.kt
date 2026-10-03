package org.bytebloom.domain.dispatch

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

data class DispatchOrder(
    val packages: List<Package>,
    val vehicle: Vehicle,
    val warehouse: Warehouse
) {
    val totalWeightKg: Double
        get() = packages.sumOf { it.weight }
}