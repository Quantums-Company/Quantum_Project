package org.bytebloom.domain.model

data class DomainGraph(
    val warehouses: List<Warehouse>,
    val packages: List<Package>,
    val routes: List<Route>,
    val vehicles: List<Vehicle>
)
