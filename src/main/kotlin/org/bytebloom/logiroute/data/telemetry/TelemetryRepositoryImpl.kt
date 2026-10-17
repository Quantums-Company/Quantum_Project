package org.bytebloom.logiroute.data.telemetry

import kotlinx.coroutines.flow.Flow
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.repository.TelemetryRepository

class TelemetryRepositoryImpl(
    private val telemetryDataSource: TelemetryDataSource
) : TelemetryRepository {

    override fun streamTelemetry(
        vehicleIds: List<String>
    ): Flow<VehicleTelemetry> = telemetryDataSource.streamTelemetry(vehicleIds)
}