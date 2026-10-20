package org.bytebloom.logiroute.domain.usecase.warehouses

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

class GetAllWarehousesUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(): List<Warehouse> =
        warehouseRepository.getAll()
}