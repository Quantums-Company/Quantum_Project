package org.bytebloom.logiroute.presentation.dependencies

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.logiroute.domain.usecase.dispatch.FindBackhaulOpportunityUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.FindCargoRecoveryPlanUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.GreedyFleetDispatchUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase

data class FleetPlanningDependencies(
    val getAllWarehouses: GetAllWarehousesUseCase,
    val getAllVehicles: GetAllVehiclesUseCase,
    val greedyFleetDispatch: GreedyFleetDispatchUseCase,
    val findBackhaulOpportunity: FindBackhaulOpportunityUseCase,
    val findCargoRecoveryPlan: FindCargoRecoveryPlanUseCase,
    val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
)