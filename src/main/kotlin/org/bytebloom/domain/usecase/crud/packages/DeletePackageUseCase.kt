package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.exception.EntityValidationException
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.validator.PackageIdValidator
import org.bytebloom.domain.validator.ValidationResult

class DeletePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): Boolean {

        when (val result = validator(id)) {
            is ValidationResult.Valid -> {
                return packageRepository.delete(id)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}