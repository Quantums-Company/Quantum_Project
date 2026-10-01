package org.bytebloom.domain.usecase.queries

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository

class FindPackagesAboveWeightUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(minimumWeightKg: Double): List<Package> {

        val packages = packageRepository.getAll()

        return packages.filter { it.weight >= minimumWeightKg }
    }
}