package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadVehicles
import org.bytebloom.data.csv.loadWarehouses
import org.bytebloom.data.mapper.VehicleMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.util.Logger

class CsvWarehouseRepository (private val csvDirectory: String = DEFAULT_CSV_DIRECTORY)
    : WarehouseRepository {
    private var achedWarehouses = listOf<Warehouse>()

    fun refresh(){
        achedWarehouses = loadWarehouses(csvDirectory).toDomain()
    }

    init {
        Logger.info("Loading vehicles in init...")
        achedWarehouses = loadWarehouses(csvDirectory).toDomain()
    }

    override fun getAll(): List<Warehouse> = achedWarehouses
}