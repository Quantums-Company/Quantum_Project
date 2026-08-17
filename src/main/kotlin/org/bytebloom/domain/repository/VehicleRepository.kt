package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Vehicle

interface VehicleRepository {
    fun getAllVehicles(): List<Vehicle>
}