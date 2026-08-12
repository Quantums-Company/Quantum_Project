package org.bytebloom.domain.repository

import org.bytebloom.data.raw.VehicleRaw

interface VehicleRepository {
    fun getAllVehicles(): List<VehicleRaw>
}