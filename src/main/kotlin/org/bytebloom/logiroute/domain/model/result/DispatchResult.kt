package org.bytebloom.logiroute.domain.model.result

import org.bytebloom.logiroute.domain.model.Vehicle

data class DispatchResult(
    val selectedVehicles: List<Vehicle>,
    val coveredZones: Set<String>,
    val uncoveredZones: Set<String>
) {
    val isFullyCovered: Boolean
        get() = uncoveredZones.isEmpty()
}