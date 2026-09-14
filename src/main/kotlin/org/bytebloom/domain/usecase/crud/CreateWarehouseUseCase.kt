package org.bytebloom.domain.usecase.crud

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(warehouse: Warehouse): Warehouse {
        return warehouseRepository.create(warehouse)
    }
}