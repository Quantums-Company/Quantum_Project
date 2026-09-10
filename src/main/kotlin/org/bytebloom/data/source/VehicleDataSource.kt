package org.bytebloom.data.source

import org.bytebloom.data.raw.VehicleRaw

interface VehicleDataSource {
    fun loadAll(): List<VehicleRaw>
}