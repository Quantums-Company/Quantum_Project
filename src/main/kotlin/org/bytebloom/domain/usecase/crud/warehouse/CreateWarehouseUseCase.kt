package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.validator.create.CreateWarehouseValidator
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.validation.EntityType

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: CreateWarehouseValidator,
    private val idGenerator: IdGenerator
) {

    suspend operator fun invoke(name: String, regionalZone: String, longitude: Double, latitude: Double): Warehouse {

        val warehouse = Warehouse(
            id = idGenerator.next(EntityType.WAREHOUSE.idPrefix),
            name = name,
            regionalZone = regionalZone,
            longitude = longitude,
            latitude = latitude )

        return when (val result = validator(warehouse)) {

            is ValidationResult.Valid -> { warehouseRepository.create(warehouse) }

            is ValidationResult.Invalid -> { throw EntityValidationException(result.violations) }
        }
    }
}