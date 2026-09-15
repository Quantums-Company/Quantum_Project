package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Vehicle

interface VehicleRepository {
    suspend fun getAll(): List<Vehicle>
    suspend fun getById(id: String): Vehicle?
    suspend fun create(vehicle: Vehicle): Vehicle
    suspend fun update(vehicle: Vehicle): Vehicle
    suspend fun delete(id: String): Boolean
}