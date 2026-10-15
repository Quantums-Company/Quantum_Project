package org.bytebloom.domain.usecase.telemetry

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import org.bytebloom.domain.model.VehicleTelemetry
import org.bytebloom.domain.model.result.VehicleDelayNotification
import org.bytebloom.domain.service.TelemetryStreamingService

class TrackActiveFleetUseCase(
    private val telemetryStreamingService: TelemetryStreamingService,
    private val delaySpeedThresholdKph: Double = DEFAULT_DELAY_SPEED_THRESHOLD_KPH
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

        return flow {
            val delayedVehicleIds = mutableSetOf<String>()

            emitAll(
                telemetryStreamingService
                    .streamTelemetry(activeVehicleIds.toList())
                    .filter { telemetry -> telemetry.vehicleId in activeVehicleIds }
                    .transform { telemetry ->
                        if (startsNewDelay(telemetry, delayedVehicleIds)) {
                            emit(telemetry.toDelayNotification())
                        }
                    }
            )
        }
    }

    private fun startsNewDelay(
        telemetry: VehicleTelemetry,
        delayedVehicleIds: MutableSet<String>
    ): Boolean =
        if (telemetry.speedKph <= delaySpeedThresholdKph) {
            delayedVehicleIds.add(telemetry.vehicleId)
        } else {
            delayedVehicleIds.remove(telemetry.vehicleId)
            false
        }

    private fun VehicleTelemetry.toDelayNotification() = VehicleDelayNotification(
        vehicleId = vehicleId,
        speedKph = speedKph,
        message = "Vehicle $vehicleId may be delayed."
    )

    private companion object {
        const val DEFAULT_DELAY_SPEED_THRESHOLD_KPH = 5.0
    }
}