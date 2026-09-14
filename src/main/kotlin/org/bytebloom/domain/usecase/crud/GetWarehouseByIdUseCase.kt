package org.bytebloom.domain.usecase.crud

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(id: String): Warehouse? {
        return warehouseRepository.getById(id)
    }
}