package org.bytebloom.domain.model.result

import org.bytebloom.domain.model.Vehicle

data class DispatchState(
    val uncoveredZones: Set<String>,
    val coveredZones: Set<String>,
    val selectedVehicles: List<Vehicle>,
    val candidateVehicles: List<Vehicle>
)