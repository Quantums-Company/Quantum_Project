package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.validator.PackageUpdateInput
import org.bytebloom.domain.validator.UpdatePackageValidator
import org.bytebloom.domain.validator.ValidationResult

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(input: PackageUpdateInput): Package {
        when (val result = validator(input)) {
            is ValidationResult.Valid -> {
                val existing = packageRepository.getById(input.id)
                    ?: throw ResourceNotFoundException("Package '${input.id}' was not found")

                val updated = Package(
                    id = existing.id,
                    weight = input.weight ?: existing.weight,
                    priority = input.priority ?: existing.priority,
                    originWarehouse = input.originWarehouse ?: existing.originWarehouse,
                    destinationWarehouse = input.destinationWarehouse ?: existing.destinationWarehouse
                )

                return packageRepository.update(updated)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}