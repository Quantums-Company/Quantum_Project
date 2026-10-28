package org.bytebloom.logiroute.domain.usecase.warehouses

import org.bytebloom.logiroute.domain.repository.SpatialWarehouseRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

class GetSpatiallyIndexedWarehousesUseCase(
    private val spatialWarehouseRepository: SpatialWarehouseRepository,
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(){
        val warehouses = warehouseRepository.getAll()
        spatialWarehouseRepository.indexAll(warehouses)
    }
}