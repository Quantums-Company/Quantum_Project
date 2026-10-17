package org.bytebloom.logiroute.domain.usecase.crud.warehouses

import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.id.WarehouseIdValidator

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): Warehouse? =
        when (val result = validator(id)) {
            is ValidatorResult.Valid -> warehouseRepository.getById(id)
            is ValidatorResult.Invalid -> throw EntityValidationException(result.violations)
        }
}