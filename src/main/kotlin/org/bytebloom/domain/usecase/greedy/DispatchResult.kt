package org.bytebloom.domain.usecase.greedy

import org.bytebloom.domain.model.Vehicle

data class DispatchResult(
    val selectedVehicles: List<Vehicle>,
    val coveredZones: Set<String>,
    val uncoveredZones: Set<String>
) {
    val isFullyCovered: Boolean
        get() = uncoveredZones.isEmpty()
}