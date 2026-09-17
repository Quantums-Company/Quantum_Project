package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.validator.PackageIdValidator
import org.bytebloom.domain.validator.ValidationResult

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository,
    private val validator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): Package? {

        when (val result = validator(id)) {
            is ValidationResult.Valid -> {
                return packageRepository.getById(id)
            }

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    result.violations.joinToString(", ")
                )
            }
        }
    }
}