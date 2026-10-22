package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.repository.TelemetrySearchRepository

/**
 * Decorator: wraps the real repository and reports when a search starts and when it is
 * cancelled before finishing, without changing what the search returns.
 */
class TracingTelemetrySearchRepository(
    private val delegate: TelemetrySearchRepository,
    private val onSearchStarted: (query: String) -> Unit,
    private val onSearchCancelled: (query: String) -> Unit
) : TelemetrySearchRepository {

    override suspend fun searchByVehicleId(query: String): List<VehicleTelemetry> {
        onSearchStarted(query)
        try {
            return delegate.searchByVehicleId(query)
        } catch (cancellation: CancellationException) {
            onSearchCancelled(query)
            throw cancellation
        }
    }
}