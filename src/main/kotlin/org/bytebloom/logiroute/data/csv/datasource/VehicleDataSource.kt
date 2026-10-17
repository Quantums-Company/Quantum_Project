package org.bytebloom.logiroute.data.csv.datasource

import org.bytebloom.logiroute.data.csv.raw.VehicleRaw

interface VehicleDataSource {
    suspend fun loadAll(): List<VehicleRaw>
}