package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.model.EntityType

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idGenerator: IdGenerator
) {

    suspend operator fun invoke(
        name: String,
        regionalZone: String,
        longitude: Double,
        latitude: Double
    ): Warehouse {
        val warehouse = Warehouse(
            id = idGenerator.next(EntityType.WAREHOUSE),
            name,
            regionalZone,
            longitude,
            latitude
        )
        return warehouseRepository.create(warehouse)
    }
}