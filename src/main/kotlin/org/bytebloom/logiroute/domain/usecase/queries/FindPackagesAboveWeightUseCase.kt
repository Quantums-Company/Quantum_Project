package org.bytebloom.logiroute.domain.usecase.queries

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.repository.PackageRepository

class FindPackagesAboveWeightUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(minimumWeightKg: Double): List<Package> {

        val packages = packageRepository.getAll()

        return packages.filter { it.weight >= minimumWeightKg }
    }
}