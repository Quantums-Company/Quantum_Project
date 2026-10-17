package org.bytebloom.presentation

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.WarehouseGraphBuilder
import org.bytebloom.domain.repository.TelemetryRepository
import org.bytebloom.domain.usecase.queries.classification.ClassifyPackageSectorUseCase
import org.bytebloom.domain.usecase.queries.routing.FindOptimalPathUseCase
import org.bytebloom.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.SupervisorJob

class CliShell : KoinComponent {

    private val warehouseRepository: WarehouseRepository by inject()
    private val routeRepository: RouteRepository by inject()
    private val packageRepository: PackageRepository by inject()
    private val vehicleRepository: VehicleRepository by inject()

    private val classifyPackage: ClassifyPackageSectorUseCase by inject()
    private val telemetryRepository: TelemetryRepository by inject()
    private val trackActiveFleet: TrackActiveFleetUseCase by inject()

    fun start() = runBlocking {
        val appJob = SupervisorJob(currentCoroutineContext()[Job])
        val appScope = CoroutineScope(appJob + Dispatchers.Default)

        var telemetryJob: Job? = null
        var routeJob: Job? = null

        try {
            println("Loading application data...")

            val data = withContext(Dispatchers.IO) {
                DataSnapshot(
                    warehouses = warehouseRepository.getAll(),
                    routes = routeRepository.getAll(),
                    packages = packageRepository.getAll(),
                    vehicles = vehicleRepository.getAll()
                )
            }

            if (data.warehouses.isEmpty()) {
                println("No warehouses available. Cannot start the CLI.")
                return@runBlocking
            }

            val graphResult = WarehouseGraphBuilder(
                warehouses = data.warehouses,
                routes = data.routes
            ).build()

            val graph = graphResult.graph

            if (graphResult.skippedRouteIds.isNotEmpty()) {
                println(
                    "Warning: skipped invalid routes: " +
                            graphResult.skippedRouteIds.joinToString()
                )
            }

            println(
                "Loaded ${data.warehouses.size} warehouses, " +
                        "${data.routes.size} routes, " +
                        "${data.packages.size} packages, " +
                        "${data.vehicles.size} vehicles."
            )

            while (true) {
                printMenu()

                when (readInput("Choose an option: ")) {
                    "1" -> {
                        if (routeJob?.isActive == true) {
                            println("Route calculation is already running.")
                            continue
                        }

                        val origin = selectWarehouse(data.warehouses, "Origin")
                            ?: continue
                        val destination =
                            selectWarehouse(data.warehouses, "Destination")
                                ?: continue

                        routeJob = appScope.launch {
                            calculateRoute(origin, destination, graph)
                        }
                    }

                    "2" -> {
                        val selectedPackage = selectPackage(data.packages)
                            ?: continue

                        appScope.launch {
                            classifySelectedPackage(selectedPackage)
                        }
                    }

                    "3" -> {
                        if (telemetryJob?.isActive == true) {
                            println("Telemetry is already running.")
                        } else if (data.vehicles.isEmpty()) {
                            println("No vehicles are available.")
                        } else {
                            telemetryJob = appScope.launch {
                                streamTelemetry(data.vehicles)
                            }
                            println("Telemetry started. Choose 4 to stop it.")
                        }
                    }

                    "4" -> {
                        val runningJob = telemetryJob
                        if (runningJob?.isActive == true) {
                            runningJob.cancelAndJoin()
                            telemetryJob = null
                            println("Telemetry stopped.")
                        } else {
                            println("Telemetry is not running.")
                        }
                    }

                    "0", null -> {
                        println("Shutting down CLI...")
                        break
                    }

                    else -> println("Invalid option. Please choose 0-4.")
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("Unable to run CLI: ${e.message}")
        } finally {
            appJob.cancelAndJoin()
            println("CLI stopped.")
        }
    }

    private fun selectWarehouse(
        warehouses: List<Warehouse>,
        label: String
    ): Warehouse? {
        println("\nAvailable warehouses:")
        warehouses.forEach { println("${it.id} - ${it.name}") }

        val id = readInput("$label warehouse ID: ") ?: return null

        return warehouses.find { it.id.equals(id, ignoreCase = true) }
            ?: run {
                println("Warehouse '$id' was not found.")
                null
            }
    }

    private fun selectPackage(
        packages: List<Package>
    ): Package? {
        if (packages.isEmpty()) {
            println("No packages are available.")
            return null
        }

        println("\nAvailable packages:")
        packages.forEachIndexed { index, pkg ->
            println("${index + 1}. ${pkg.id} | ${pkg.weight} kg")
        }

        val choice = readInput("Choose package number: ")?.toIntOrNull()

        return if (choice != null && choice in 1..packages.size) {
            packages[choice - 1]
        } else {
            println("Invalid package number.")
            null
        }
    }

    private suspend fun calculateRoute(
        origin: Warehouse,
        destination: Warehouse,
        graph: WarehouseGraph
    ) {
        val findOptimalPath = getKoin().get<FindOptimalPathUseCase> {
            parametersOf(graph)
        }

        coroutineScope {
            val spinner = launch {
                val frames = listOf("|", "/", "-", "\\")
                var index = 0

                while (isActive) {
                    print("\rCalculating shortest route ${frames[index % frames.size]}")
                    System.out.flush()
                    index++
                    delay(100.milliseconds)
                }
            }

            try {
                val path = withContext(Dispatchers.Default) {
                    findOptimalPath(origin, destination)
                }

                if (path == null) {
                    println("\rNo route exists between these warehouses.       ")
                    return@coroutineScope
                }

                val totalDistance = path.zipWithNext().sumOf { (from, to) ->
                    graph.adjacencyMap[from]?.get(to) ?: 0.0
                }

                println("\rShortest route found:                         ")
                println(path.joinToString(" -> ") { "${it.id} (${it.name})" })
                println("Total distance: %.2f km".format(totalDistance))
            } finally {
                spinner.cancelAndJoin()
                println()
            }
        }
    }

    private suspend fun classifySelectedPackage(pkg: Package) {
        try {
            println("\nClassifying ${pkg.id}...")

            val region = withContext(Dispatchers.Default) {
                classifyPackage(pkg)
            }

            println("Predicted sector for ${pkg.id}: $region")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("Classification failed: ${e.message}")
        }
    }

    private suspend fun streamTelemetry(vehicles: List<Vehicle>) {
        val vehicleIds = vehicles.map { it.id }.toSet()

        try {
            coroutineScope {
                launch {
                    telemetryRepository
                        .streamTelemetry(vehicleIds.toList())
                        .collect { telemetry ->
                            println(
                                "\n[TELEMETRY] ${telemetry.vehicleId} | " +
                                        "GPS: (${telemetry.latitude}, ${telemetry.longitude}) | " +
                                        "Speed: ${telemetry.speedKph} km/h | " +
                                        "Fuel: ${telemetry.fuelPercent}%"
                            )
                        }
                }

                launch {
                    trackActiveFleet(vehicleIds).collect { notification ->
                        println("\n[DELAY ALERT] ${notification.message}")
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("\nTelemetry stopped due to an error: ${e.message}")
        }
    }

    private fun readInput(prompt: String): String? {
        print(prompt)
        System.out.flush()
        return readlnOrNull()?.trim()
    }

    private fun printMenu() {
        println(
            """
            
            ========== LogiRoute CLI ==========
            1. Calculate shortest route
            2. Classify a package (KNN)
            3. Start real-time fleet telemetry
            4. Stop telemetry
            0. Exit
            ==================================
            """.trimIndent()
        )
    }

    private data class DataSnapshot(
        val warehouses: List<Warehouse>,
        val routes: List<Route>,
        val packages: List<Package>,
        val vehicles: List<Vehicle>
    )
}