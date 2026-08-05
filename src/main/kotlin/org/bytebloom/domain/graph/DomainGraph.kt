package org.bytebloom.domain.graph

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

data class DomainGraph(
    val warehouses: List<Warehouse>,
    val packages: List<Package>,
    val routes: List<Route>,
    val vehicles: List<Vehicle>
)
