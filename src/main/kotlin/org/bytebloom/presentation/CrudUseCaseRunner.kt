package org.bytebloom.presentation

import org.bytebloom.domain.model.exception.DatabaseConflictException
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.exception.UnknownDataException
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.usecase.crud.packages.CreatePackageUseCase
import org.bytebloom.domain.usecase.crud.packages.DeletePackageUseCase
import org.bytebloom.domain.usecase.crud.packages.GetPackageByIdUseCase
import org.bytebloom.domain.usecase.crud.packages.UpdatePackageUseCase
import org.bytebloom.domain.usecase.crud.route.CreateRouteUseCase
import org.bytebloom.domain.usecase.crud.route.DeleteRouteUseCase
import org.bytebloom.domain.usecase.crud.route.GetRouteByIdUseCase
import org.bytebloom.domain.usecase.crud.route.UpdateRouteUseCase
import org.bytebloom.domain.usecase.crud.vehicle.CreateVehicleUseCase
import org.bytebloom.domain.usecase.crud.vehicle.DeleteVehicleUseCase
import org.bytebloom.domain.usecase.crud.vehicle.GetVehicleByIdUseCase
import org.bytebloom.domain.usecase.crud.vehicle.UpdateVehicleUseCase
import org.bytebloom.domain.usecase.crud.warehouse.CreateWarehouseUseCase
import org.bytebloom.domain.usecase.crud.warehouse.DeleteWarehouseUseCase
import org.bytebloom.domain.usecase.crud.warehouse.GetWarehouseByIdUseCase
import org.bytebloom.domain.usecase.crud.warehouse.UpdateWarehouseUseCase
import org.bytebloom.domain.validator.CreatePackageValidator
import org.bytebloom.domain.validator.CreateRouteValidator
import org.bytebloom.domain.validator.CreateVehicleValidator
import org.bytebloom.domain.validator.CreateWarehouseValidator
import org.bytebloom.domain.validator.PackageIdValidator
import org.bytebloom.domain.validator.PackageUpdateInput
import org.bytebloom.domain.validator.RouteIdValidator
import org.bytebloom.domain.validator.RouteUpdateInput
import org.bytebloom.domain.validator.UpdatePackageValidator
import org.bytebloom.domain.validator.UpdateRouteValidator
import org.bytebloom.domain.validator.UpdateVehicleValidator
import org.bytebloom.domain.validator.UpdateWarehouseValidator
import org.bytebloom.domain.validator.VehicleIdValidator
import org.bytebloom.domain.validator.VehicleUpdateInput
import org.bytebloom.domain.validator.WarehouseIdValidator
import org.bytebloom.domain.validator.WarehouseUpdateInput

class CrudUseCaseRunner(
    warehouseRepository: WarehouseRepository,
    vehicleRepository: VehicleRepository,
    routeRepository: RouteRepository,
    packageRepository: PackageRepository
) {
    private val createWarehouse = CreateWarehouseUseCase(warehouseRepository, CreateWarehouseValidator())
    private val getWarehouseById = GetWarehouseByIdUseCase(warehouseRepository, WarehouseIdValidator())
    private val updateWarehouse = UpdateWarehouseUseCase(warehouseRepository, UpdateWarehouseValidator())
    private val deleteWarehouse = DeleteWarehouseUseCase(warehouseRepository, WarehouseIdValidator())

    private val createVehicle = CreateVehicleUseCase(vehicleRepository, CreateVehicleValidator())
    private val getVehicleById = GetVehicleByIdUseCase(vehicleRepository, VehicleIdValidator())
    private val updateVehicle = UpdateVehicleUseCase(vehicleRepository, UpdateVehicleValidator())
    private val deleteVehicle = DeleteVehicleUseCase(vehicleRepository, VehicleIdValidator())

    private val createRoute = CreateRouteUseCase(routeRepository, CreateRouteValidator())
    private val getRouteById = GetRouteByIdUseCase(routeRepository, RouteIdValidator())
    private val updateRoute = UpdateRouteUseCase(routeRepository, UpdateRouteValidator())
    private val deleteRoute = DeleteRouteUseCase(routeRepository, RouteIdValidator())

    private val createPackage = CreatePackageUseCase(packageRepository, CreatePackageValidator())
    private val getPackageById = GetPackageByIdUseCase(packageRepository, PackageIdValidator())
    private val updatePackage = UpdatePackageUseCase(packageRepository, UpdatePackageValidator())
    private val deletePackage = DeletePackageUseCase(packageRepository, PackageIdValidator())

    suspend fun runAll() {
        println("=== CRUD Use Case Verification ===\n")

        val origin = safely("create origin warehouse") {
            createWarehouse(Warehouse("WH-DEMO-1", "Demo Origin", "ZoneA", 35.0, 32.0))
        }
        val destination = safely("create destination warehouse") {
            createWarehouse(Warehouse("WH-DEMO-2", "Demo Destination", "ZoneB", 36.0, 33.0))
        }

        if (origin != null && destination != null) {
            runWarehouseReadUpdate(origin)
            val vehicle = runVehicleCycle(origin)
            val route = runRouteCycle(origin, destination)
            val pkg = runPackageCycle(origin, destination)

            // cleanup in FK-safe order: dependents first, warehouses last
            pkg?.let { safely("delete package") { deletePackage(it.id) } }
            route?.let { safely("delete route") { deleteRoute(it.id) } }
            vehicle?.let { safely("delete vehicle") { deleteVehicle(it.id) } }
            safely("delete origin warehouse") { deleteWarehouse(origin.id) }
            safely("delete destination warehouse") { deleteWarehouse(destination.id) }
        }

        println("\n--- Deliberately invalid input (proves error strategy) ---")
        runDeliberatelyInvalidCreate()
    }

    private suspend fun runWarehouseReadUpdate(warehouse: Warehouse) {
        safely("get warehouse by id") {
            println("Warehouse fetched: ${getWarehouseById(warehouse.id)}")
        }
        safely("update warehouse") {
            println("Warehouse updated: ${updateWarehouse(WarehouseUpdateInput(id = warehouse.id, name = "Updated Demo Warehouse"))}")
        }
    }

    private suspend fun runVehicleCycle(warehouse: Warehouse): Vehicle? = safely("vehicle CRUD cycle") {
        val created = createVehicle(Vehicle("TRK-DEMO-1", 500.0, 2.5, warehouse))
        println("Vehicle created: ${created.id}")
        println("Vehicle fetched: ${getVehicleById(created.id)?.id}")
        val updated = updateVehicle(VehicleUpdateInput(id = created.id, costPerKm = 3.0))
        println("Vehicle updated: costPerKm=${updated.costPerKm}")
        updated
    }

    private suspend fun runRouteCycle(origin: Warehouse, destination: Warehouse): Route? = safely("route CRUD cycle") {
        val created = createRoute(Route("RT-DEMO-1", 120.0, 15, origin, destination))
        println("Route created: ${created.id}")
        println("Route fetched: ${getRouteById(created.id)?.id}")
        val updated = updateRoute(RouteUpdateInput(id = created.id, distanceKm = 130.0))
        println("Route updated: distanceKm=${updated.distanceKm}")
        updated
    }

    private suspend fun runPackageCycle(origin: Warehouse, destination: Warehouse): Package? = safely("package CRUD cycle") {
        val created = createPackage(Package("PKG-DEMO-1", 12.5, Priority.STANDARD, origin, destination))
        println("Package created: ${created.id}")
        println("Package fetched: ${getPackageById(created.id)?.id}")
        val updated = updatePackage(PackageUpdateInput(id = created.id, priority = Priority.URGENT))
        println("Package updated: priority=${updated.priority}")
        updated
    }

    private suspend fun runDeliberatelyInvalidCreate() {
        try {
            createWarehouse(Warehouse(id = "", name = "", regionalZone = "", longitude = 999.0, latitude = 999.0))
            println("Unexpected: invalid warehouse was accepted!")
        } catch (e: Exception) {
            println("Correctly rejected -> ${formatError(e)}")
        }
    }

    private suspend fun <T> safely(step: String, block: suspend () -> T): T? =
        try {
            block()
        } catch (e: Exception) {
            println("[$step] ${formatError(e)}")
            null
        }

    private fun formatError(e: Throwable): String = when (e) {
        is EntityValidationException -> "Validation failed: ${e.violations.joinToString("; ")}"
        is ResourceNotFoundException -> "Not found: ${e.message}"
        is DatabaseConflictException -> "Conflict: ${e.message}"
        is NetworkUnavailableException -> "Network issue: ${e.message}"
        is UnknownDataException -> "Unexpected data error: ${e.message}"
        else -> "Unhandled error: ${e.message}"
    }
}