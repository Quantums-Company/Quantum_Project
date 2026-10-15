package org.bytebloom.presentation

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.usecase.queries.classification.ClassifyPackageSectorUseCase
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.dijkstra.DijkstraRouter
import org.bytebloom.domain.usecase.queries.routing.FindOptimalPathUseCase


class CliShell : KoinComponent {
    private val classifyPackage: ClassifyPackageSectorUseCase by inject()

    fun start(
        packages: List<Package>,
        startWarehouse: Warehouse,
        destinationWarehouse: Warehouse
    ) = runBlocking {
        val appJob = SupervisorJob()
        val appScope = CoroutineScope(appJob + Dispatchers.Default)
        val graph = WarehouseGraph()

        val distanceKm = calculateDistanceKm(
            startWarehouse,
            destinationWarehouse
        )

        graph.addRoute(
            startWarehouse,
            destinationWarehouse,
            distanceKm
        )

        val findOptimalPath = FindOptimalPathUseCase(
            routeFinder = DijkstraRouter(graph)
        )
        var telemetryJob: Job? = null

        try {
            while (true) {
                println("\n===== Delivery System =====")
                println("1. Calculate Route")
                println("2. Classify Package")
                println("3. Stream Fleet Telemetry")
                println("4. Exit")
                print("Choose an option: ")

                when (readlnOrNull()?.trim()) {
                    "1" -> appScope.launch {
                        try {
                            val spinner = launch {
                                print("Calculating route")
                                repeat(3) {
                                    delay(500)
                                    print(".")
                                }
                            }

                            val path = findOptimalPath(
                                startWarehouse,
                                destinationWarehouse
                            )

                            spinner.cancelAndJoin()

                            if (path != null) {
                                println("\nRoute found:")
                                path.forEach { warehouse ->
                                    println(warehouse.name)
                                }
                                println("Distance: $distanceKm km")
                            } else {
                                println("\nNo route found.")
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            println("\nRoute calculation failed: ${e.message}")
                        }
                    }


                    "2" -> {
                        if (packages.isEmpty()) {
                            println("No packages available.")
                        } else {
                            println("Available packages:")
                            packages.forEachIndexed { index, pkg ->
                                println("${index + 1}. ${pkg.id}")
                            }

                            print("Choose package number: ")
                            val choice = readlnOrNull()?.trim()?.toIntOrNull()

                            if (choice != null && choice in 1..packages.size) {
                                val selectedPackage = packages[choice - 1]

                                appScope.launch {
                                    try {
                                        val region = classifyPackage(selectedPackage)
                                        println(
                                            "Package ${selectedPackage.id} classified as: $region"
                                        )
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (e: Exception) {
                                        println(
                                            "Classification unavailable: ${e.message}"
                                        )
                                    }
                                }
                            } else {
                                println("Invalid package number.")
                            }
                        }
                    }


                    "3" -> {
                        if (telemetryJob?.isActive == true) {
                            println("Telemetry is already running.")
                        } else {
                            telemetryJob = appScope.launch {
                                var latitude = startWarehouse.latitude
                                var longitude = startWarehouse.longitude
                                var speed = 40
                                var fuel = 85

                                try {
                                    flow {
                                        while (true) {
                                            emit(
                                                "Vehicle TRK-001 | " +
                                                    "GPS: ($latitude, $longitude) | " +
                                                    "Speed: $speed km/h | Fuel: $fuel%"
                                            )

                                            latitude += 0.001
                                            longitude += 0.001
                                            speed = if (speed >= 60) 40 else speed + 5
                                            fuel = if (fuel <= 20) 85 else fuel - 1

                                            delay(2000)
                                        }
                                    }.collect { message ->
                                        println(message)
                                    }
                                } catch (e: CancellationException) {
                                    throw e
                                }
                            }
                            println("Telemetry started.")
                        }
                    }

                    "4" -> {
                        println("Exiting application...")
                        break
                    }

                    else -> println("Invalid option. Try again.")
                }
            }
        } finally {
            appScope.cancel()
            appJob.cancel()
        }
    }
    private fun calculateDistanceKm(
        start: Warehouse,
        destination: Warehouse
    ): Double {
        val earthRadiusKm = 6371.0

        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(destination.latitude)
        val deltaLat = Math.toRadians(destination.latitude - start.latitude)
        val deltaLon = Math.toRadians(destination.longitude - start.longitude)

        val a = kotlin.math.sin(deltaLat / 2).let { it * it } +
                kotlin.math.cos(lat1) * kotlin.math.cos(lat2) *
                kotlin.math.sin(deltaLon / 2).let { it * it }

        return 2 * earthRadiusKm * kotlin.math.asin(
            kotlin.math.sqrt(a.coerceIn(0.0, 1.0))
        )
    }
}