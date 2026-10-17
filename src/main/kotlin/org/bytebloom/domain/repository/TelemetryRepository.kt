package org.bytebloom.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bytebloom.domain.model.VehicleTelemetry

interface TelemetryRepository {

    fun streamTelemetry(
        vehicleIds: List<String>
    ): Flow<VehicleTelemetry>
}