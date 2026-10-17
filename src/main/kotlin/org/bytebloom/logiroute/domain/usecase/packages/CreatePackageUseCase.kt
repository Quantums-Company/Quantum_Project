package org.bytebloom.logiroute.domain.usecase.packages

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.bytebloom.logiroute.domain.model.EntityType

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val idGenerator: IdGenerator
) {
    suspend operator fun invoke(
        weight: Double,
        priority: Priority,
        originWarehouse: Warehouse,
        destinationWarehouse: Warehouse
    ): Package {

        val pkg = Package(
            id = idGenerator.next(EntityType.PACKAGE),
            weight = weight,
            priority = priority,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        return packageRepository.create(pkg)
    }
}