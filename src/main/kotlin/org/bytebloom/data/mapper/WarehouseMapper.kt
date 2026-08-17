package org.bytebloom.data.mapper




import org.bytebloom.data.raw.PackageRaw
//import org.bytebloom.data.raw.RouteRaw
//import org.bytebloom.data.raw.VehicleRaw
//import org.bytebloom.data.raw.WarehouseRaw
//import org.bytebloom.domain.model.Package
//import org.bytebloom.domain.model.Route
//import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
//import org.bytebloom.util.Logger
//
object WarehouseMapper {

    fun toDomain(raw: WarehouseRaw): Warehouse {
        return Warehouse(
            raw.id,
            raw.name,
            raw.regionalZone,
            raw.longitude,
            raw.latitude
        )
    }
}

//private const val VEHICLE = "Vehicle"
//private const val PACKAGE = "Package"
//private const val ROUTE = "Route"
//
//
//
//    private fun findWarehouse(
//        warehouseMap: Map<String, Warehouse>,
//        warehouseId: String,
//        owner: String
//    ): Warehouse? {
//
//        val warehouse = warehouseMap[warehouseId]
//
//        if (warehouse == null) {
//            Logger.warning(
//                "$owner references unknown warehouse '$warehouseId'."
//            )
//        }
//
//        return warehouse
//    }
//
//    private fun buildWarehouses(
//        warehouseRaws: List<WarehouseRaw>
//    ): Map<String, Warehouse> {
//
//        return warehouseRaws.associateBy(
//            keySelector = { it.id },
//            valueTransform = { raw ->
//                Warehouse(
//                    raw.id,
//                    raw.name,
//                    raw.regionalZone,
//                    raw.longitude,
//                    raw.latitude,
//                )
//            }
//        )
//    }
//
//    private fun buildVehicles(
//        vehicleRaws: List<VehicleRaw>,
//        warehouseMap: Map<String, Warehouse>
//    ): List<Vehicle> {
//        return vehicleRaws.mapNotNull { raw ->
//            val warehouse = findWarehouse(
//                warehouseMap = warehouseMap,
//                warehouseId = raw.currentWarehouseId,
//                owner = VEHICLE
//            ) ?: return@mapNotNull null
//
//            val vehicle = Vehicle(
//                raw.id,
//                raw.maxCapacityKg,
//                raw.costPerKm,
//                warehouse
//            )
//
//            warehouse.addVehicle(vehicle)
//            vehicle
//        }
//    }
//
//    private fun buildPackages(
//        packageRaws: List<PackageRaw>,
//        warehouseMap: Map<String, Warehouse>
//    ): List<org.bytebloom.domain.model.Package> {
//        return packageRaws.mapNotNull { raw ->
//            val origin = findWarehouse(
//                warehouseMap,
//                raw.originWarehouseId,
//                PACKAGE)
//            val destination = findWarehouse(
//                warehouseMap,
//                raw.destinationWarehouseId,
//                PACKAGE)
//
//            if (origin == null || destination == null) {
//                return@mapNotNull null
//            }
//            val pkg = Package(
//                raw.id,
//                raw.weight,
//                raw.priority,
//                origin,
//                destination).
//            also(origin::addPackage)
//            pkg
//        }
//    }
//
//    private fun buildRoutes(
//        routeRaws: List<RouteRaw>,
//        warehouseMap: Map<String, Warehouse>
//    ): List<Route> {
//        return routeRaws.mapNotNull { raw ->
//            val origin = findWarehouse(
//                warehouseMap,
//                raw.originWarehouseId,
//                ROUTE)
//            val destination = findWarehouse(
//                warehouseMap,
//                raw.destinationWarehouseId,
//                ROUTE)
//
//            if (origin == null || destination == null) {
//                return@mapNotNull null
//            }
//            val route = Route(
//                raw.id,
//                raw.distanceKm,
//                raw.typicalDelayMin,
//                origin,
//                destination).
//            also(origin::addRoute)
//            route
//        }
//    }