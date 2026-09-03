package org.bytebloom.domain.usecase.queries

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository

class FindPackagesByDestinationUseCase(
    private val packageRepository: PackageRepository
) {

    operator fun invoke(
        destinationWarehouse: Warehouse
    ): List<Package> =
        packageRepository
            .getAll()
            .filter {
                it.destinationWarehouse.id == destinationWarehouse.id
            }
}