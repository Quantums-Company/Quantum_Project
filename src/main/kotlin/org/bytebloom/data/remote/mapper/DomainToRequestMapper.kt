package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseRequestDto
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

object DomainToRequestMapper {

    fun Warehouse.toRequest(): WarehouseRequestDto =
        WarehouseRequestDto(
            id = this.id,
            name = this.name,
            regionalZone = this.regionalZone,
            longitude = this.longitude,
            latitude = this.latitude
        )

    fun Vehicle.toRequest(): VehicleRequestDto =
        VehicleRequestDto(
            id = this.id,
            currentWarehouseId = this.currentWarehouse.id,
            maxCapacityKg = this.maxCapacityKg,
            costPerKm = this.costPerKm
        )

    fun Route.toRequest(): RouteRequestDto =
        RouteRequestDto(
            id = this.id,
            originWarehouseId = this.originWarehouse.id,
            destinationWarehouseId = this.destinationWarehouse.id,
            distanceKm = this.distanceKm,
            typicalDelayMin = this.typicalDelayMin
        )

    fun Package.toRequest(): PackageRequestDto =
        PackageRequestDto(
            id = this.id,
            weight = this.weight,
            originWarehouseId = this.originWarehouse.id,
            destinationWarehouseId = this.destinationWarehouse.id,
            priority = this.priority.name
        )

    fun toWarehouseRequests(
        warehouses: List<Warehouse>
    ): List<WarehouseRequestDto> =
        warehouses.map{ warehouse -> warehouse.toRequest() }

    fun toVehicleRequests(
        vehicles: List<Vehicle>
    ): List<VehicleRequestDto> =
        vehicles.map{ vehicle -> vehicle.toRequest() }

    fun toRouteRequests(
        routes: List<Route>
    ): List<RouteRequestDto> =
        routes.map{ route -> route.toRequest() }

    fun toPackageRequests(
        packages: List<Package>
    ): List<PackageRequestDto> =
        packages.map{ pkg -> pkg.toRequest() }
}