package org.bytebloom.logiroute.domain.usecase.warehouses

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.SpatialWarehouseRepository


class GetNearbyWarehousesUseCase(
    private val spatialWarehouseRepository: SpatialWarehouseRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): List<Warehouse> =
        spatialWarehouseRepository.getNearby(lat, lon)
}