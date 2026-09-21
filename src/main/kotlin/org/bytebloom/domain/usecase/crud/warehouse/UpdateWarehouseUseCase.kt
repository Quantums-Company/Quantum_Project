package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.validator.UpdateWarehouseValidator
import org.bytebloom.domain.validator.ValidationResult
import org.bytebloom.domain.validator.WarehouseUpdateInput

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: UpdateWarehouseValidator
) {
    suspend operator fun invoke(input: WarehouseUpdateInput): Warehouse {
        when (val result = validator(input)) {
            is ValidationResult.Valid -> {
                val existing = warehouseRepository.getById(input.id)
                    ?: throw ResourceNotFoundException("Warehouse '${input.id}' was not found")

                val updated = Warehouse(
                    id = existing.id,
                    name = input.name ?: existing.name,
                    regionalZone = input.regionalZone ?: existing.regionalZone,
                    longitude = input.longitude ?: existing.longitude,
                    latitude = input.latitude ?: existing.latitude
                )

                return warehouseRepository.update(updated)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}