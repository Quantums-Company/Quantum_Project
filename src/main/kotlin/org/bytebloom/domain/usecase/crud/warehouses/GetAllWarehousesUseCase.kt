package org.bytebloom.domain.usecase.crud.warehouses

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository

class GetAllWarehousesUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(): List<Warehouse> =
        warehouseRepository.getAll()
}