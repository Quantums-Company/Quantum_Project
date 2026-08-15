package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadVehicles
import org.bytebloom.data.raw.VehicleRaw
import org.bytebloom.domain.repository.VehicleRepository

class CsvVehicleRepository : VehicleRepository {
    override fun getAllVehicles(): List<VehicleRaw> {
        return loadVehicles()
    }
}