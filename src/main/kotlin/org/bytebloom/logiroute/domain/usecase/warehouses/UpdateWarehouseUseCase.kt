package org.bytebloom.logiroute.domain.usecase.warehouses

import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.validator.update.UpdateWarehouseValidator
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.model.input.update.WarehouseUpdateInput

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: UpdateWarehouseValidator
) {
    suspend operator fun invoke(input: WarehouseUpdateInput): Warehouse {
        when (val result = validator(input)) {
            is ValidatorResult.Valid -> {
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

            is ValidatorResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}