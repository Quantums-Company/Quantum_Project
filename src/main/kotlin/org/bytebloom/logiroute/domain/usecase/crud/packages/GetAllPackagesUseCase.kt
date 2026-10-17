package org.bytebloom.logiroute.domain.usecase.crud.packages

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.repository.PackageRepository

class GetAllPackagesUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(): List<Package> =
        packageRepository.getAll()
}