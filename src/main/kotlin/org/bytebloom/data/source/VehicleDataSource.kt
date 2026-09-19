package org.bytebloom.data.source

import org.bytebloom.data.raw.VehicleRaw

interface VehicleDataSource {
    suspend fun loadAll(): List<VehicleRaw>
    suspend fun getById(): List<VehicleRaw>
    suspend fun create(): List<VehicleRaw>
    suspend fun update(): List<VehicleRaw>
    suspend fun delete(): List<VehicleRaw>
}