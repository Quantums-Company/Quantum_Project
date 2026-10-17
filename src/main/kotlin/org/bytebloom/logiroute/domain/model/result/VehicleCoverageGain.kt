package org.bytebloom.logiroute.domain.model.result

import org.bytebloom.logiroute.domain.model.Vehicle

data class VehicleCoverageGain(
    val vehicle: Vehicle,
    val newZones: Set<String>
)