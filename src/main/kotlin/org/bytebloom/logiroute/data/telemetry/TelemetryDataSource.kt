package org.bytebloom.logiroute.data.telemetry

import kotlinx.coroutines.flow.Flow
import org.bytebloom.logiroute.domain.model.VehicleTelemetry

interface TelemetryDataSource {

    fun streamTelemetry(
        vehicleIds: List<String>
    ): Flow<VehicleTelemetry>
}