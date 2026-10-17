package org.bytebloom.logiroute.domain.usecase.crud.packages

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.PackageRepository

class FindPackagesByDestinationUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(
        destinationWarehouse: Warehouse
    ): List<Package> =
        packageRepository
            .getAll()
            .filter {
                it.destinationWarehouse.id == destinationWarehouse.id
            }
}