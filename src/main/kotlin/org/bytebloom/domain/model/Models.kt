package org.bytebloom.domain.model

import org.bytebloom.data.dataHolder.Priority
import org.bytebloom.domain.model.logic.quickSortCargoByWeight

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

class Package(
    val id: String,
    val weight: Double,
    val priority: Priority,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse
)
//{
//    override fun toString(): String {
//        return "Package(id='$id', weight=$weight, priority=$priority, origin:${origin.id}, destination=${destination.id})"
//    }
//}

class Route(
    val id: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse
)
//{
//    override fun toString(): String {
//        return "Route(id='$id', distanceKm=$distanceKm, typicalDelayMin=$typicalDelayMin, origin:${origin.id}, destination=${destination.id})"
//    }
//}

class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
)
//{
//    override fun toString(): String {
//        return "Vehicle(id='$id', maxCapacityKg=$maxCapacityKg, costPerKm=$costPerKm, currentHub:${currentHub.id})"
//    }
//}