package org.bytebloom.logiroute.domain.usecase.warehouses

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.SpatialWarehouseRepository

class AddWarehouseToSpatialIndexUseCase(
    private val spatialWarehouseRepository: SpatialWarehouseRepository
) {
    suspend operator fun invoke(warehouse: Warehouse) {
        spatialWarehouseRepository.addWarehouse(warehouse)
    }
}