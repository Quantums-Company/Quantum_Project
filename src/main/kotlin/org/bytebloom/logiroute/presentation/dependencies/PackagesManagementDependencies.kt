package org.bytebloom.logiroute.presentation.dependencies

import org.bytebloom.logiroute.domain.usecase.packages.CreatePackageUseCase
import org.bytebloom.logiroute.domain.usecase.packages.DeletePackageUseCase
import org.bytebloom.logiroute.domain.usecase.packages.FindPackagesAboveWeightUseCase
import org.bytebloom.logiroute.domain.usecase.packages.FindPackagesByDestinationUseCase
import org.bytebloom.logiroute.domain.usecase.packages.FindPackagesByPriorityUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetPackageByIdUseCase
import org.bytebloom.logiroute.domain.usecase.packages.UpdatePackageUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase

data class PackagesManagementDependencies(
    val getAllPackages: GetAllPackagesUseCase,
    val getPackageById: GetPackageByIdUseCase,
    val createPackage: CreatePackageUseCase,
    val updatePackage: UpdatePackageUseCase,
    val deletePackage: DeletePackageUseCase,
    val findPackagesAboveWeight: FindPackagesAboveWeightUseCase,
    val findPackagesByDestination: FindPackagesByDestinationUseCase,
    val findPackagesByPriority: FindPackagesByPriorityUseCase,
    val getAllWarehouses: GetAllWarehousesUseCase
)