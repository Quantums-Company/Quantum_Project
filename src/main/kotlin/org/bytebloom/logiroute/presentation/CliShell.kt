package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.repository.TelemetryRepository
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.WarehouseGraphBuilder
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

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

            var isRunning = true
            while (isRunning) {
                printMenu()
                val choice = readInput("Choose an option: ")
                if (choice == "0" || choice == null) {
                    println("Shutting down CLI...")
                    isRunning = false
                } else {
                    val result = processOption(choice, routeJob, telemetryJob, appScope, data, graph)
                    routeJob = result.routeJob
                    telemetryJob = result.telemetryJob
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            println("Unable to run CLI due to IO error: ${e.message}")
        } catch (e: IllegalStateException) {
            println("Unable to run CLI due to state error: ${e.message}")
        } finally {
            appJob.cancelAndJoin()
            println("CLI stopped.")
        }
    }

    private suspend fun processOption(
        choice: String,
        currentRouteJob: Job?,
        currentTelemetryJob: Job?,
        appScope: CoroutineScope,
        data: DataSnapshot,
        graph: WarehouseGraph
    ): OptionState {
        var newRouteJob = currentRouteJob
        var newTelemetryJob = currentTelemetryJob

        when (choice) {
            "1" -> newRouteJob = executeRouteCalculation(currentRouteJob, appScope, data.warehouses, graph)
            "2" -> executePackageClassification(appScope, data.packages)
            "3" -> newTelemetryJob = executeTelemetryStart(currentTelemetryJob, appScope, data.vehicles)
            "4" -> newTelemetryJob = executeTelemetryStop(currentTelemetryJob)
            else -> println("Invalid option. Please choose 0-4.")
        }

        return OptionState(newRouteJob, newTelemetryJob)
    }

    private fun executeRouteCalculation(
        currentJob: Job?,
        scope: CoroutineScope,
        warehouses: List<Warehouse>,
        graph: WarehouseGraph
    ): Job? {
        if (currentJob?.isActive == true) {
            println("Route calculation is already running.")
            return currentJob
        }

        val origin = selectWarehouse(warehouses, "Origin") ?: return currentJob
        val destination = selectWarehouse(warehouses, "Destination") ?: return currentJob

        return scope.launch {
            calculateRoute(origin, destination, graph)
        }
    }

    private fun executePackageClassification(scope: CoroutineScope, packages: List<Package>) {
        val selectedPackage = selectPackage(packages) ?: return
        scope.launch {
            classifySelectedPackage(selectedPackage)
        }
    }

    private fun executeTelemetryStart(
        currentJob: Job?,
        scope: CoroutineScope,
        vehicles: List<Vehicle>
    ): Job? {
        if (currentJob?.isActive == true) {
            println("Telemetry is already running.")
            return currentJob
        }
        if (vehicles.isEmpty()) {
            println("No vehicles are available.")
            return null
        }
        val job = scope.launch {
            streamTelemetry(vehicles)
        }
        println("Telemetry started. Choose 4 to stop it.")
        return job
    }

    private suspend fun executeTelemetryStop(currentJob: Job?): Job? {
        return if (currentJob?.isActive == true) {
            currentJob.cancelAndJoin()
            println("Telemetry stopped.")
            null
        } else {
            println("Telemetry is not running.")
            currentJob
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
        } catch (e: IllegalArgumentException) {
            println("Classification failed due to invalid arguments: ${e.message}")
        } catch (e: IllegalStateException) {
            println("Classification failed due to state issue: ${e.message}")
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
        } catch (e: IllegalStateException) {
            println("\nTelemetry stopped due to state issue: ${e.message}")
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

    private data class OptionState(
        val routeJob: Job?,
        val telemetryJob: Job?
    )

    private data class DataSnapshot(
        val warehouses: List<Warehouse>,
        val routes: List<Route>,
        val packages: List<Package>,
        val vehicles: List<Vehicle>
    )
}