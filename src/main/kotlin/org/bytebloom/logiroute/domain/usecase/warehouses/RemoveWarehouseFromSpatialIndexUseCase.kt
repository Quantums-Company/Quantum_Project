package org.bytebloom.logiroute.domain.usecase.warehouses

import org.bytebloom.logiroute.domain.repository.SpatialWarehouseRepository

class RemoveWarehouseFromSpatialIndexUseCase(
    private val spatialWarehouseRepository: SpatialWarehouseRepository
) {
    suspend operator fun invoke(warehouseId: String) {
        spatialWarehouseRepository.remove(warehouseId)
    }
}