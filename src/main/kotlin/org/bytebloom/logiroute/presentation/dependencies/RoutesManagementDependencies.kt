package org.bytebloom.logiroute.presentation.dependencies

import org.bytebloom.logiroute.domain.usecase.routes.CreateRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routes.DeleteRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetRouteByIdUseCase
import org.bytebloom.logiroute.domain.usecase.routes.UpdateRouteUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase

data class RoutesManagementDependencies(
    val getAllRoutes: GetAllRoutesUseCase,
    val getRouteById: GetRouteByIdUseCase,
    val createRoute: CreateRouteUseCase,
    val updateRoute: UpdateRouteUseCase,
    val deleteRoute: DeleteRouteUseCase,
    val getAllWarehouses: GetAllWarehousesUseCase
)