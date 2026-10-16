package org.bytebloom.data.source.telemetry

import kotlinx.coroutines.flow.Flow
import org.bytebloom.domain.model.VehicleTelemetry

interface TelemetryDataSource {

    fun streamTelemetry(
        vehicleIds: List<String>
    ): Flow<VehicleTelemetry>
}