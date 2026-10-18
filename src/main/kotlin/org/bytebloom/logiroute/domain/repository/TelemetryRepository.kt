package org.bytebloom.logiroute.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bytebloom.logiroute.domain.model.VehicleTelemetry

interface TelemetryRepository {

    fun streamTelemetry(
        vehicleIds: List<String>
    ): Flow<VehicleTelemetry>
}