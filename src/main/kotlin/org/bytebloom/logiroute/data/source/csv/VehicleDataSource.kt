package org.bytebloom.logiroute.data.source.csv

import org.bytebloom.logiroute.data.raw.VehicleRaw

interface VehicleDataSource {
    suspend fun loadAll(): List<VehicleRaw>
    suspend fun getById(): List<VehicleRaw>
    suspend fun create(): List<VehicleRaw>
    suspend fun update(): List<VehicleRaw>
    suspend fun delete(): List<VehicleRaw>
}