package org.bytebloom.domain.service

import kotlinx.coroutines.flow.Flow
import org.bytebloom.domain.model.VehicleTelemetry

interface TelemetryStreamingService {

    fun streamTelemetry(
        vehicleIds: List<String>
    ): Flow<VehicleTelemetry>
}