package org.bytebloom.domain.model

import org.bytebloom.domain.sorting.quickSortCargoByWeight

class Warehouse(
    val id: String,
    val name: String,
    val regionalZone: String,
    val longitude: Double,
    val latitude: Double
) {
    private val _cargoQueue = mutableListOf<Package>()
    val cargoQueue: List<Package>
        get() = _cargoQueue

    private val _outgoingRoutes = mutableListOf<Route>()
    val outgoingRoutes: List<Route>
        get() = _outgoingRoutes

    private val _stationedVehicles = mutableListOf<Vehicle>()
    val stationedVehicles: List<Vehicle>
        get() = _stationedVehicles

    fun sortCargoByWeight() {
        quickSortCargoByWeight(_cargoQueue)
    }

    fun addPackage(pkg: Package) {
        _cargoQueue.add(pkg)
    }

    fun addRoute(route: Route) {
        _outgoingRoutes.add(route)
    }

    fun addVehicle(vehicle: Vehicle) {
        _stationedVehicles.add(vehicle)
    }

    override fun toString(): String {
        return "Warehouse(id='$id', name=$name, regionalZone=$regionalZone, longitude:$longitude, latitude=$latitude)"
    }
}