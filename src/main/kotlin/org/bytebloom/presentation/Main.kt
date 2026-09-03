package org.bytebloom.presentation

import org.bytebloom.data.repository.CsvPackageRepository
import org.bytebloom.data.repository.CsvRouteRepository
import org.bytebloom.data.repository.CsvVehicleRepository
import org.bytebloom.data.repository.CsvWarehouseRepository
import org.bytebloom.domain.tree.binary.AVLTree
import org.bytebloom.domain.tree.binary.BST
import org.bytebloom.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.domain.performance.TreePerformanceReport
import org.bytebloom.domain.performance.TreeSearchResult
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository

private const val DEMO_ROUTE_ORIGIN_ID = "WH-031"
private const val DEMO_ROUTE_DESTINATION_ID = "WH-091"

private const val FRAGILE_HANDLING_SURCHARGE = 10.0
private const val COLD_CHAIN_MULTIPLIER = 1.25
private const val EXPRESS_INSURANCE_SURCHARGE = 20.0

class AnalyzeTreePerformanceUseCase(
    private val trackingIdGenerator: PackageTrackingIdGenerator,
    private val binarySearchTree: BST<String>,
    private val avlTree: AVLTree<String>
) {

    operator fun invoke(
        packageCount: Int,
        targetTrackingIds: List<String>
    ): TreePerformanceReport {

        val trackingIds =
            trackingIdGenerator.generate(packageCount)

        trackingIds.forEach {
            binarySearchTree.insert(it)
            avlTree.insert(it)
        }

        val results =
            targetTrackingIds.map { trackingId ->
                TreeSearchResult(
                    trackingId = trackingId,
                    binarySearchTreeSteps =
                        binarySearchTree.search(trackingId),
                    avlTreeSteps =
                        avlTree.search(trackingId)
                )
            }

        return TreePerformanceReport(
            totalPackages = packageCount,
            results = results
        )
    }
}

fun main() {
    val warehouseRepo: WarehouseRepository = CsvWarehouseRepository()
    val warehousesById = warehouseRepo.getAll().associateBy { it.id }

    val packageRepo: PackageRepository = CsvPackageRepository(warehousesById)
    val routeRepo: RouteRepository = CsvRouteRepository(warehousesById)
    val vehicleRepo: VehicleRepository = CsvVehicleRepository(warehousesById)
}

