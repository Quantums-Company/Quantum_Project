package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Vehicle

interface VehicleRepository {
    fun getAll(): List<Vehicle>
    fun getById(id: String): Vehicle?
    fun create(vehicle: Vehicle): Vehicle
    fun update(vehicle: Vehicle): Vehicle
    fun delete(id: String): Boolean
}