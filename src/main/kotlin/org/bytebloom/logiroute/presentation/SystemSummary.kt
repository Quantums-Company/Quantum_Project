package org.bytebloom.logiroute.presentation

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse

/** Everything the system currently knows about, loaded in one go. */
data class SystemSummary(
    val warehouses: List<Warehouse>,
    val routes: List<Route>,
    val vehicles: List<Vehicle>,
    val packages: List<Package>
)
