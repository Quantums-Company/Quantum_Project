package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Vehicle

interface VehicleRepository {
    fun getAll(): List<Vehicle>
}