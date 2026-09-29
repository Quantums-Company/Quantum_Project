package org.bytebloom.domain.model.result

import org.bytebloom.domain.model.Vehicle

data class VehicleCoverageGain(
    val vehicle: Vehicle,
    val newZones: Set<String>
)