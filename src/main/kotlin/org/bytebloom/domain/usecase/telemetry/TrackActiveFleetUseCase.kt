package org.bytebloom.domain.usecase.telemetry

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.bytebloom.domain.model.result.VehicleDelayNotification
import org.bytebloom.domain.service.TelemetryStreamingService

class TrackActiveFleetUseCase(
    private val telemetryStreamingService: TelemetryStreamingService,
    private val delaySpeedThresholdKph: Double = 5.0
) {

    init {
        require(delaySpeedThresholdKph >= 0.0) {
            "Delay speed threshold cannot be negative."
        }
    }

    operator fun invoke(
        activeVehicleIds: Set<String>
    ): Flow<VehicleDelayNotification> {
        if (activeVehicleIds.isEmpty()) {
            return emptyFlow()
        }

        return telemetryStreamingService
            .streamTelemetry(activeVehicleIds.toList())
            .filter { telemetry ->
                telemetry.vehicleId in activeVehicleIds &&
                        telemetry.speedKph <= delaySpeedThresholdKph
            }
            .map { telemetry ->
                VehicleDelayNotification(
                    vehicleId = telemetry.vehicleId,
                    speedKph = telemetry.speedKph,
                    message = "Vehicle ${telemetry.vehicleId} may be delayed."
                )
            }
    }
}
