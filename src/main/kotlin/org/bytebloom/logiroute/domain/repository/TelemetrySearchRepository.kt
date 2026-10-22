package org.bytebloom.logiroute.domain.repository

import org.bytebloom.logiroute.domain.model.VehicleTelemetry

interface TelemetrySearchRepository {

    suspend fun searchByVehicleId(query: String): List<VehicleTelemetry>
}