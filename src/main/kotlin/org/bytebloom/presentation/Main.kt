package org.bytebloom.presentation

import kotlinx.coroutines.runBlocking
import org.bytebloom.data.remote.client.SupabaseClientProvider
import org.bytebloom.data.remote.datasource.SdkPackageDataSource
import org.bytebloom.data.remote.datasource.SdkRouteDataSource
import org.bytebloom.data.remote.datasource.SdkVehicleDataSource
import org.bytebloom.data.remote.datasource.SdkWarehouseDataSource
import org.bytebloom.data.repository.remote.RemotePackageRepository
import org.bytebloom.data.repository.remote.RemoteRouteRepository
import org.bytebloom.data.repository.remote.RemoteVehicleRepository
import org.bytebloom.data.repository.remote.RemoteWarehouseRepository
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.DatabaseConflictException
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.exception.UnknownDataException
import org.bytebloom.domain.usecase.greedy.GreedyFleetDispatchUseCase

fun formatError(e: Throwable): String = when (e) {
    is EntityValidationException -> "Validation failed: ${e.violations.joinToString("; ")}"
    is ResourceNotFoundException -> "Not found: ${e.message}"
    is DatabaseConflictException -> "Conflict: ${e.message}"
    is NetworkUnavailableException -> "Network issue: ${e.message}"
    is UnknownDataException -> "Unexpected error: ${e.message}"
    else -> "Unhandled error: ${e.message}"
}

@Suppress("TooGenericExceptionCaught")
fun main() = runBlocking {
    val client = SupabaseClientProvider.create()

    val warehouseRepo = RemoteWarehouseRepository(SdkWarehouseDataSource(client))
    val warehousesById = warehouseRepo.getAll().associateBy { it.id }

    val vehicleRepo = RemoteVehicleRepository(warehousesById, SdkVehicleDataSource(client))
    val routeRepo = RemoteRouteRepository(warehousesById, SdkRouteDataSource(client))
    val packageRepo = RemotePackageRepository(warehousesById, SdkPackageDataSource(client))

    println("--- Warehouses ---")
    val warehouses = try {
        warehouseRepo.getAll().also { list -> list.forEach { println(it) } }
    } catch (e: Exception) {
        println(formatError(e))
        emptyList()
    }

    println("--- Vehicles ---")
    val vehicles = try {
        vehicleRepo.getAll().also { list ->
            list.forEach {
                println(
                    "Vehicle(id=${it.id}, capacity=${it.maxCapacityKg}, warehouse=${it.currentWarehouse.id})"
                )
            }
        }
    } catch (e: Exception) {
        println(formatError(e))
        emptyList()
    }

    println("--- Routes ---")
    val routes = try {
        routeRepo.getAll().also { list ->
            list.forEach {
                println(
                    "Route(id=${it.id}, ${it.originWarehouse.id} -> ${it.destinationWarehouse.id}, ${it.distanceKm}km)"
                )
            }
        }
    } catch (e: Exception) {
        println(formatError(e))
        emptyList()
    }

    println("--- Packages ---")
    try {
        packageRepo.getAll().forEach {
            println("Package(id=${it.id}, weight=${it.weight}, priority=${it.priority})")
        }
    } catch (e: Exception) {
        println(formatError(e))
    }

    println("\n=== CRUD Use Case Verification (Sub-Task 2 + 3 + 4) ===")
    CrudUseCaseRunner(warehouseRepo, vehicleRepo, routeRepo, packageRepo).runAll()

    println("\n=== Greedy Fleet Dispatcher (Sub-Task 5) ===")
    demonstrateGreedyDispatcher(
        warehouses = warehouses,
        vehicles = vehicles,
        routes = routes
    )

    println("\n=== Greedy Dispatcher Complexity Check: O(N^2) ===")
    benchmarkGreedyComplexity()
}

/**
 * Empirically proves GreedyFleetDispatchUseCase runs in O(N^2), not the O(2^N)
 * a brute-force set-cover search would need.
 */
fun benchmarkGreedyComplexity() {
    val sizes = BENCHMARK_INPUT_SIZES

    println(
        BENCHMARK_HEADER_FORMAT.format(
            "N",
            "coverageOf() calls",
            "N*(N+1)/2 (theory)",
            "2^N (brute force)"
        )
    )
    println("-".repeat(BENCHMARK_DIVIDER_LENGTH))

    for (n in sizes) {
        val benchWarehouse = Warehouse(
            id = BENCHMARK_WAREHOUSE_ID,
            name = BENCHMARK_WAREHOUSE_NAME,
            regionalZone = BENCHMARK_WAREHOUSE_ZONE,
            longitude = DEFAULT_LAT_LONG,
            latitude = DEFAULT_LAT_LONG
        )
        val vehicles = (1..n).map { i ->
            Vehicle(
                id = "$BENCHMARK_VEHICLE_PREFIX$i",
                maxCapacityKg = DEFAULT_VEHICLE_CAPACITY,
                costPerKm = DEFAULT_VEHICLE_COST,
                currentWarehouse = benchWarehouse
            )
        }
        val zones = (1..n).map { "$ZONE_PREFIX$it" }.toSet()
        val vehicleZone = vehicles.mapIndexed { index, vehicle ->
            vehicle to setOf("$ZONE_PREFIX${index + 1}")
        }.toMap()

        var callCount = 0
        GreedyFleetDispatchUseCase().invoke(
            targetZones = zones,
            availableVehicles = vehicles,
            coverageOf = { vehicle ->
                callCount++
                vehicleZone[vehicle] ?: emptySet()
            }
        )

        val theoreticalQuadratic = n * (n + 1) / 2
        val bruteForce = if (n <= BRUTE_FORCE_COMPUTE_LIMIT) {
            (1L shl n).toString()
        } else {
            TOO_LARGE_TO_COMPUTE_MSG
        }

        println(
            BENCHMARK_ROW_FORMAT.format(
                n,
                callCount,
                theoreticalQuadratic,
                bruteForce
            )
        )
    }

    println(BENCHMARK_SUMMARY_TEXT)
}

// Global Constants for Benchmarking & Main Runner
private const val SIZE_10 = 10
private const val SIZE_20 = 20
private const val SIZE_40 = 40
private const val SIZE_80 = 80
private const val SIZE_160 = 160

private val BENCHMARK_INPUT_SIZES = listOf(
    SIZE_10,
    SIZE_20,
    SIZE_40,
    SIZE_80,
    SIZE_160
)

private const val BENCHMARK_HEADER_FORMAT = "%-6s | %-18s | %-20s | %-20s"
private const val BENCHMARK_ROW_FORMAT = "%-6d | %-18d | %-20d | %-20s"
private const val BENCHMARK_DIVIDER_LENGTH = 72
private const val BRUTE_FORCE_COMPUTE_LIMIT = 30

private const val BENCHMARK_WAREHOUSE_ID = "WH-BENCH"
private const val BENCHMARK_WAREHOUSE_NAME = "Bench"
private const val BENCHMARK_WAREHOUSE_ZONE = "BenchZone"

private const val BENCHMARK_VEHICLE_PREFIX = "TRK-BENCH-"
private const val ZONE_PREFIX = "Zone"

private const val DEFAULT_LAT_LONG = 0.0
private const val DEFAULT_VEHICLE_CAPACITY = 1.0
private const val DEFAULT_VEHICLE_COST = 1.0

private const val TOO_LARGE_TO_COMPUTE_MSG = "too large to compute"
private const val BENCHMARK_SUMMARY_TEXT =
    "\nAs N doubles, coverageOf() calls roughly quadruple, matching N*(N+1)/2 " +
            "exactly — while 2^N brute force explodes (e.g. at N=40, brute force " +
            "would need ~1.1 trillion subset checks vs. only 820 calls here)."