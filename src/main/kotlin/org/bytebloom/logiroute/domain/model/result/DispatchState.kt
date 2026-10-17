package org.bytebloom.logiroute.domain.model.result

import org.bytebloom.logiroute.domain.model.Vehicle

data class DispatchState(
    val uncoveredZones: Set<String>,
    val coveredZones: Set<String>,
    val selectedVehicles: List<Vehicle>,
    val candidateVehicles: List<Vehicle>
)