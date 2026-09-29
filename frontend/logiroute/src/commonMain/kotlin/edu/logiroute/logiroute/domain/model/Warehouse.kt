package edu.logiroute.logiroute.domain.model

data class Warehouse(
    val id: String,
    val name: String,
    val regionalZone: String,
    val longitude: Double,
    val latitude: Double,
    val cargoQueue: List<Package> = emptyList(),
    val outgoingRoutes: List<Route> = emptyList(),
    val stationedVehicles: List<Vehicle> = emptyList()
){

    fun findHighestPriorityCargo(): Package? {
        return cargoQueue.minByOrNull { pkg ->
            Priority.from(pkg.priority).ordinal
        }
    }
}
