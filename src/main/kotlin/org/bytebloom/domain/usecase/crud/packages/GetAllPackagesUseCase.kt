package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository

class GetAllPackagesUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(): List<Package> =
        packageRepository.getAll()
}