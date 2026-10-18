package org.bytebloom.logiroute.presentation.model

data class VehicleTelemetryUi(
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKph: Double,
    val fuelPercent: Double
)

sealed interface FleetEventUi {
    val sequence: Long

    data class Reading(
        override val sequence: Long,
        val telemetry: VehicleTelemetryUi
    ) : FleetEventUi

    data class DelayAlert(
        override val sequence: Long,
        val vehicleId: String,
        val speedKph: Double
    ) : FleetEventUi
}
