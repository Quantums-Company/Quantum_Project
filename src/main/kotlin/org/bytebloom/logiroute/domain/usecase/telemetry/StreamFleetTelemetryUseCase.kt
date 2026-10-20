package org.bytebloom.logiroute.domain.usecase.telemetry

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.repository.TelemetryRepository

class StreamFleetTelemetryUseCase(
    private val telemetryRepository: TelemetryRepository
) {

    operator fun invoke(
        vehicleIds: Set<String>
    ): Flow<VehicleTelemetry> {
        if (vehicleIds.isEmpty()) {
            return emptyFlow()
        }

        return telemetryRepository
            .streamTelemetry(vehicleIds.toList())
            .filter { telemetry -> telemetry.vehicleId in vehicleIds }
    }
}