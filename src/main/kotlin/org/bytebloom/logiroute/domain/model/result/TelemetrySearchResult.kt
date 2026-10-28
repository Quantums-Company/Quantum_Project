package org.bytebloom.logiroute.domain.model.result

import org.bytebloom.logiroute.domain.model.VehicleTelemetry

data class TelemetrySearchResult(
    val query: String,
    val matches: List<VehicleTelemetry>
)