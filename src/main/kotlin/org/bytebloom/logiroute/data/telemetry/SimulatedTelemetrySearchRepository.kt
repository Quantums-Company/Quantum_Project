package org.bytebloom.logiroute.data.telemetry

import kotlinx.coroutines.delay
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.repository.TelemetrySearchRepository
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SimulatedTelemetrySearchRepository(
    private val latestTelemetry: List<VehicleTelemetry> = DEFAULT_LATEST_TELEMETRY,
    private val networkLatency: Duration = DEFAULT_NETWORK_LATENCY
) : TelemetrySearchRepository {

    init {
        require(!networkLatency.isNegative()) {
            "Network latency cannot be negative."
        }
    }

    override suspend fun searchByVehicleId(query: String): List<VehicleTelemetry> {
        delay(networkLatency)

        return latestTelemetry.filter { telemetry ->
            telemetry.vehicleId.contains(query, ignoreCase = true)
        }
    }

    private companion object {
        val DEFAULT_NETWORK_LATENCY = 500.milliseconds

        val DEFAULT_LATEST_TELEMETRY = listOf(
            VehicleTelemetry("TRK-001", 31.50, 35.00, 42.0, 80.0),
            VehicleTelemetry("TRK-002", 31.51, 35.01, 0.0, 64.0),
            VehicleTelemetry("VAN-010", 31.52, 35.02, 55.0, 91.0)
        )
    }
}