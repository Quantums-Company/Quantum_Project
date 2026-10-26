package org.bytebloom.logiroute.presentation.dependencies

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.logiroute.domain.usecase.dispatch.AddVehicleToHubUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.AssignPackageToCargoQueueUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchCargoUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.OptimizeCargoLoadUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase

data class DispatchDependencies(
    val getAllWarehouses: GetAllWarehousesUseCase,
    val getAllVehicles: GetAllVehiclesUseCase,
    val getAllPackages: GetAllPackagesUseCase,
    val assignPackageToCargoQueue: AssignPackageToCargoQueueUseCase,
    val addVehicleToHub: AddVehicleToHubUseCase,
    val dispatchVehicle: DispatchVehicleUseCase,
    val dispatchCargo: DispatchCargoUseCase,
    val optimizeCargoLoad: OptimizeCargoLoadUseCase,
    val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
)