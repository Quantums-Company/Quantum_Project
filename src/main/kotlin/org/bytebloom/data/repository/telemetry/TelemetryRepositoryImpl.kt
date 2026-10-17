package org.bytebloom.data.repository.telemetry

import kotlinx.coroutines.flow.Flow
import org.bytebloom.data.source.telemetry.TelemetryDataSource
import org.bytebloom.domain.model.VehicleTelemetry
import org.bytebloom.domain.repository.TelemetryRepository

class TelemetryRepositoryImpl(
    private val telemetryDataSource: TelemetryDataSource
) : TelemetryRepository {

    override fun streamTelemetry(
        vehicleIds: List<String>
    ): Flow<VehicleTelemetry> = telemetryDataSource.streamTelemetry(vehicleIds)
}