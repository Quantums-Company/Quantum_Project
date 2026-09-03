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

    fun addPackage(pkg: Package): Boolean =
        _cargoQueue.add(pkg)

    fun removePackage(pkg: Package): Boolean =
        _cargoQueue.remove(pkg)

    fun addRoute(route: Route) : Boolean =
        _outgoingRoutes.add(route)


    fun addVehicle(vehicle: Vehicle) : Boolean =
        _stationedVehicles.add(vehicle)

    fun removeVehicle(vehicle: Vehicle): Boolean =
        _stationedVehicles.remove(vehicle)

    fun containsPackage(pkg: Package): Boolean = pkg in _cargoQueue

    fun hasVehicle(vehicle: Vehicle): Boolean = vehicle in _stationedVehicles

    fun sortCargoByWeight() {
        quickSortCargoByWeight(_cargoQueue)
    }

    override fun toString(): String {
        return "Warehouse(" +
                "id='$id', " +
                "name=$name, " +
                "regionalZone=$regionalZone, " +
                "longitude=$longitude, " +
                "latitude=$latitude" +
                ")"
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Warehouse) return false

        return id == other.id &&
                name == other.name
    }
}