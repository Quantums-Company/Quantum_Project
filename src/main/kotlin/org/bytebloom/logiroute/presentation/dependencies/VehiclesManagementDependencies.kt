package org.bytebloom.logiroute.presentation.dependencies

import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.CreateVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.DeleteVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.FindCheapestSuitableVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.FindStationedVehiclesByCapacityUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetVehicleByIdUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.UpdateVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase

data class VehiclesManagementDependencies(
    val getAllVehicles: GetAllVehiclesUseCase,
    val getVehicleById: GetVehicleByIdUseCase,
    val createVehicle: CreateVehicleUseCase,
    val updateVehicle: UpdateVehicleUseCase,
    val deleteVehicle: DeleteVehicleUseCase,
    val findStationedVehiclesByCapacity: FindStationedVehiclesByCapacityUseCase,
    val findCheapestSuitableVehicle: FindCheapestSuitableVehicleUseCase,
    val getAllWarehouses: GetAllWarehousesUseCase,
    val getAllPackages: GetAllPackagesUseCase
)