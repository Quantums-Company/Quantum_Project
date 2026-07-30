package org.bytebloom.domain.model

import org.bytebloom.dataHolder.Priority
import org.bytebloom.logic.quickSortCargo

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
        quickSortCargo(_cargoQueue)
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
}

class Package(
    val id: String,
    val weight: Double,
    val priority: Priority,
    val origin: Warehouse,
    val destination: Warehouse
)

class Route(
    val routeId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val origin: Warehouse,
    val destination: Warehouse
)

class Vehicle(
    val vehicleId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
)