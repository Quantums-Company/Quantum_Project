package org.bytebloom.presentation

import org.bytebloom.di.networkModule
import org.bytebloom.di.repositoryModule
import org.bytebloom.di.useCaseModule
import org.bytebloom.di.validatorModule
import org.bytebloom.domain.dispatch.DispatchOrder
import org.bytebloom.domain.dispatch.StandardDispatchProcessor
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.shipment.ShipmentTracker
import org.bytebloom.domain.usecase.queries.knapsack.OptimizeCargoLoadUseCase
import org.koin.core.context.startKoin

private const val VEHICLE_MAX_CAPACITY_KG = 30.0
private const val VEHICLE_COST_PER_KM = 2.0
private const val VEHICLE_MAX_VOLUME_M3 = 1.0

private const val PKG_1_WEIGHT = 10.0
private const val PKG_2_WEIGHT = 20.0
private const val PKG_3_WEIGHT = 15.0
private const val PKG_4_WEIGHT = 5.0

private const val PKG_1_VOLUME_M3 = 0.20
private const val PKG_2_VOLUME_M3 = 0.90
private const val PKG_3_VOLUME_M3 = 0.35
private const val PKG_4_VOLUME_M3 = 0.10

private const val WAREHOUSE_MAIN_LON = 35.95
private const val WAREHOUSE_MAIN_LAT = 32.22
private const val WAREHOUSE_DEST_LON = 35.93
private const val WAREHOUSE_DEST_LAT = 32.20

fun main() {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            useCaseModule
        )
    }.koin

    println("=== Sub-Task 5: End-to-End Verification ===")

    val warehouse = Warehouse(
        id = "WH-001",
        name = "Main Warehouse",
        regionalZone = "North",
        longitude = WAREHOUSE_MAIN_LON,
        latitude = WAREHOUSE_MAIN_LAT
    )

    val destination = Warehouse(
        id = "WH-002",
        name = "Destination Warehouse",
        regionalZone = "South",
        longitude = WAREHOUSE_DEST_LON,
        latitude = WAREHOUSE_DEST_LAT
    )

    val packages = listOf(
        Package(
            "PKG-001",
            PKG_1_WEIGHT,
            Priority.URGENT,
            warehouse,
            destination
        ),
        Package(
            "PKG-002",
            PKG_2_WEIGHT,
            Priority.STANDARD,
            warehouse,
            destination
        ),
        Package(
            "PKG-003",
            PKG_3_WEIGHT,
            Priority.URGENT,
            warehouse,
            destination
        ),
        Package(
            "PKG-004",
            PKG_4_WEIGHT,
            Priority.LOW,
            warehouse,
            destination
        )
    )

    packages.forEach(warehouse::addPackage)

    val vehicle = Vehicle(
        id = "TRK-001",
        maxCapacityKg = VEHICLE_MAX_CAPACITY_KG,
        costPerKm = VEHICLE_COST_PER_KM,
        currentWarehouse = warehouse
    )

    warehouse.addVehicle(vehicle)

    val optimizeCargoLoadUseCase =
        koin.get<OptimizeCargoLoadUseCase>()

    /*
     * Original 1D optimization:
     * weight constraint only.
     */
    val weightOnlyResult = optimizeCargoLoadUseCase(
        vehicle = vehicle,
        candidatePackages = packages
    )

    /*
     * Bonus 2D optimization:
     * weight + physical volume.
     */
    val volumeByPackageId = mapOf(
        "PKG-001" to PKG_1_VOLUME_M3,
        "PKG-002" to PKG_2_VOLUME_M3,
        "PKG-003" to PKG_3_VOLUME_M3,
        "PKG-004" to PKG_4_VOLUME_M3
    )

    val volumeAwareResult =
        optimizeCargoLoadUseCase.optimizeWithVolume(
            vehicle = vehicle,
            candidatePackages = packages,
            volumeByPackageId = volumeByPackageId,
            maxVolumeM3 = VEHICLE_MAX_VOLUME_M3
        )

    println("\n=== Weight-Only Optimization ===")
    println(
        "Selected cargo: " +
                weightOnlyResult.selectedItems.joinToString { it.id }
    )
    println(
        "Total weight: ${weightOnlyResult.totalWeightKg} kg"
    )
    println(
        "Total volume: ${weightOnlyResult.totalVolumeM3} m³"
    )
    println(
        "Total priority: ${weightOnlyResult.totalPriorityValue}"
    )

    println("\n=== Weight + Volume Optimization ===")
    println(
        "Selected cargo: " +
                volumeAwareResult.selectedItems.joinToString { it.id }
    )
    println(
        "Total weight: ${volumeAwareResult.totalWeightKg} kg"
    )
    println(
        "Total volume: ${volumeAwareResult.totalVolumeM3} m³"
    )
    println(
        "Total priority: ${volumeAwareResult.totalPriorityValue}"
    )

    println("\n=== Constraint Trade-off ===")
    println(
        "Weight-only selection: " +
                weightOnlyResult.selectedItems.joinToString { it.id }
    )
    println(
        "Volume-aware selection: " +
                volumeAwareResult.selectedItems.joinToString { it.id }
    )

    /*
     * Dispatch continues to use the original weight-only result.
     * The 2D optimization is demonstrated independently above.
     */
    val selectedPackages = packages.filter { pkg ->
        weightOnlyResult.selectedItems.any { item ->
            item.id == pkg.id
        }
    }

    println("\nSelected packages for dispatch:")
    selectedPackages.forEach {
        println("${it.id} - ${it.weight}kg - ${it.priority}")
    }

    val shipmentTracker = ShipmentTracker()
    val dispatcher = StandardDispatchProcessor(shipmentTracker)

    val order = DispatchOrder(
        packages = selectedPackages,
        vehicle = vehicle,
        warehouse = warehouse
    )

    val outcome = dispatcher.dispatch(order)

    println("\nDispatch outcome: $outcome")

    println("\nShipment states:")

    selectedPackages.forEach { pkg ->
        println(
            "${pkg.id}: " +
                    shipmentTracker.historyOf(pkg.id)
                        .map { it.name }
        )
    }
}