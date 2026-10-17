package org.bytebloom.logiroute.domain.usecase.crud.packages

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.id.PackageIdValidator

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository,
    private val validator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): Package? =
        when (val result = validator(id)) {
            is ValidatorResult.Valid -> packageRepository.getById(id)
            is ValidatorResult.Invalid -> throw EntityValidationException(result.violations)
        }
}