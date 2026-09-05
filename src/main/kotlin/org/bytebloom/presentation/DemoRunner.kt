package org.bytebloom.presentation

import org.bytebloom.domain.commandPattern.AssignPackageToQueueCommand
import org.bytebloom.domain.commandPattern.CommandInvoker
import org.bytebloom.domain.commandPattern.DispatchVehicleCommand
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.tree.binary.AVLTree
import org.bytebloom.domain.tree.binary.BST
import org.bytebloom.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.routing.WarehouseGraphBuilder
import org.bytebloom.domain.routing.bfs.BidirectionalBreadthFirstRouter
import org.bytebloom.domain.routing.dijkstra.DijkstraRouter
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.tree.hierarchicalHub.HubTreeBuilder
import org.bytebloom.domain.usecase.TraceHubLineageUseCase
import org.bytebloom.domain.usecase.commands.AddVehicleToHubUseCase
import org.bytebloom.domain.usecase.commands.AssignPackageToCargoQueueUseCase
import org.bytebloom.domain.usecase.commands.DispatchVehicleUseCase
import org.bytebloom.domain.usecase.commands.ReroutePackageUseCase
import org.bytebloom.domain.usecase.queries.FindCheapestSuitableVehicleUseCase
import org.bytebloom.domain.usecase.queries.FindPackagesAboveWeightUseCase
import org.bytebloom.domain.usecase.queries.FindPackagesByDestinationUseCase
import org.bytebloom.domain.usecase.queries.FindPackagesByPriorityUseCase
import org.bytebloom.domain.usecase.queries.FindStationedVehiclesByCapacityUseCase
import org.bytebloom.domain.usecase.queries.GetWarehouseLoadFactorUseCase
import org.bytebloom.domain.usecase.queries.backhaul.FindBackhaulOpportunityUseCase
import org.bytebloom.domain.usecase.queries.planing.FindCargoRecoveryPlanUseCase
import org.bytebloom.domain.usecase.queries.reporting.GetWarehouseReportUseCase
import org.bytebloom.domain.usecase.queries.routing.FindAllPairsShortestPathUseCase
import org.bytebloom.domain.usecase.queries.routing.FindFewestHopsRouteUseCase
import org.bytebloom.domain.usecase.queries.routing.FindOptimalPathUseCase
import org.bytebloom.domain.usecase.queries.routing.VerifyHubLinkUseCase
import org.bytebloom.domain.usecase.queries.shipment.EstimateShipmentDeliveryUseCase
import org.bytebloom.domain.usecase.queries.backhaul.BackhaulOpportunity

class DemoRunner(
    private val warehouseRepository: WarehouseRepository,
    private val packageRepository: PackageRepository,
    private val routeRepository: RouteRepository,
    private val vehicleRepository: VehicleRepository
) {
    companion object {
        private const val CONSOLE_WIDTH = 72
        private const val TRACKING_ID_COUNT = 1000
        private const val DISPLAYED_PAIRS_LIMIT = 10
        private const val PERCENTAGE_MULTIPLIER = 100.0
    }

    private val warehouses =
        warehouseRepository.getAll()

    private val packages =
        packageRepository.getAll()

    private val vehicles =
        vehicleRepository.getAll()

    private val routes =
        routeRepository.getAll()

    private val warehouseGraph =
        WarehouseGraphBuilder(
            warehouses = warehouses,
            routes = routes
        ).build()

    private val dijkstraRouter: RouteFinder =
        DijkstraRouter(warehouseGraph)

    private val bfsRouter: RouteFinder =
        BidirectionalBreadthFirstRouter(warehouseGraph)

    private val findOptimalPath =
        FindOptimalPathUseCase(dijkstraRouter)

    private val findFewestHops =
        FindFewestHopsRouteUseCase(bfsRouter)

    private val verifyHubLink =
        VerifyHubLinkUseCase(dijkstraRouter)

    private val findAllPairsShortestPath =
        FindAllPairsShortestPathUseCase(
            routeFinder = dijkstraRouter,
            graph = warehouseGraph
        )

    private val findStationedVehicles =
        FindStationedVehiclesByCapacityUseCase()

    private val findCheapestVehicle =
        FindCheapestSuitableVehicleUseCase(
            vehicleRepository
        )

    private val findPackagesAboveWeight =
        FindPackagesAboveWeightUseCase(
            packageRepository
        )

    private val findPackagesByDestination =
        FindPackagesByDestinationUseCase(
            packageRepository
        )

    private val findPackagesByPriority =
        FindPackagesByPriorityUseCase(
            packageRepository
        )

    private val getWarehouseLoadFactor =
        GetWarehouseLoadFactorUseCase()

    private val getWarehouseReport =
        GetWarehouseReportUseCase(
            warehouseRepository
        )

    private val findBackhaulOpportunity =
        FindBackhaulOpportunityUseCase(
            packageRepository
        )

    private val findCargoRecoveryPlan =
        FindCargoRecoveryPlanUseCase()

    private val estimateShipmentDelivery =
        EstimateShipmentDeliveryUseCase(
            packageRepository = packageRepository,
            routeRepository = routeRepository,
            findOptimalPath = findOptimalPath
        )

    private val traceHubLineage =
        TraceHubLineageUseCase(
            HubTreeBuilder(
                getWarehouseLoadFactor
            ).build(warehouses)
        )

    private val addVehicleToHub =
        AddVehicleToHubUseCase()

    private val assignPackageToQueue =
        AssignPackageToCargoQueueUseCase()

    private val dispatchVehicle =
        DispatchVehicleUseCase()

    private val reroutePackage =
        ReroutePackageUseCase()

    private val commandInvoker =
        CommandInvoker()

    fun run() {
        printHeader()

        runWarehouseQueries()
        runPackageQueries()
        runVehicleQueries()
        runRoutingQueries()
        runShipmentQueries()
        runBackhaulQuery()
        runRecoveryQuery()
        runReportingQuery()
        runTreeDemo()
        runCommandPatternDemo()
        runAddVehicleToHubDemo()
        runReroutePackageDemo()

        printFooter()
    }

    // -------------------------------------------------------------------------
    // Warehouse Queries
    // -------------------------------------------------------------------------

    private fun runWarehouseQueries() {

        printSection("WAREHOUSE QUERIES")

        val warehouse =
            warehouses.firstOrNull()

        if (warehouse == null) {
            printResult(
                "Warehouse Queries",
                "No warehouses are available."
            )
            return
        }

        printResult(
            "Get Warehouse Load Factor",
            """
            Warehouse: ${warehouse.id}
            Load Factor: ${formatPercent(getWarehouseLoadFactor(warehouse))}
            """.trimIndent()
        )

        printResult(
            "Get Warehouse Report",
            getWarehouseReport(warehouse.id)
                ?.let {
                    """
                    Warehouse: ${it.warehouseId}
                    Package Count: ${it.packageCount}
                    Total Package Weight: ${formatKg(it.totalPackageWeight)}
                    Total Vehicle Capacity: ${formatKg(it.totalVehicleCapacity)}
                    """.trimIndent()
                }
                ?: "Warehouse '${warehouse.id}' was not found."
        )
    }

    // -------------------------------------------------------------------------
    // Package Queries
    // -------------------------------------------------------------------------
    private fun runPackageQueries() {

        printSection("PACKAGE QUERIES")

        printResult(
            "Find Packages Above Weight",
            findPackagesAboveWeight(
                minimumWeightKg = PERCENTAGE_MULTIPLIER
            ).formatPackages()
        )

        val destinationWarehouse =
            warehouses.getOrNull(1)

        printResult(
            "Find Packages By Destination",
            destinationWarehouse?.let {
                findPackagesByDestination(it).formatPackages()
            } ?: "No destination warehouse is available."
        )

        printResult(
            "Find Packages By Priority",
            findPackagesByPriority(
                priority = Priority.URGENT
            ).formatPackages()
        )
    }
    // -------------------------------------------------------------------------
    // Vehicle Queries
    // -------------------------------------------------------------------------

    private fun runVehicleQueries() {

        printSection("VEHICLE QUERIES")

        val warehouse =
            warehouses.firstOrNull()

        if (warehouse == null) {
            printResult(
                "Vehicle Queries",
                "No warehouses are available."
            )
            return
        }

        val requiredCapacityKg =
            packages
                .take(2)
                .sumOf(Package::weight)

        printResult(
            "Find Stationed Vehicles By Capacity",
            findStationedVehicles(
                warehouse = warehouse,
                requiredCapacityKg = requiredCapacityKg
            ).formatVehicles()
        )

        val candidatePackages =
            packages
                .take(2)

        printResult(
            "Find Cheapest Suitable Vehicle",
            findCheapestVehicle(
                packages = candidatePackages
            )?.formatVehicle()
                ?: "No suitable vehicle found."
        )
    }

    // -------------------------------------------------------------------------
    // Routing
    // -------------------------------------------------------------------------

    private fun runRoutingQueries() {

        printSection("ROUTING")

        val origin =
            warehouses.getOrNull(0)

        val destination =
            warehouses.getOrNull(1)

        if (origin == null || destination == null) {
            printResult(
                "Routing",
                "At least two warehouses are required."
            )
            return
        }

        val optimalPath =
            findOptimalPath(
                origin,
                destination
            )

        printResult(
            "Find Optimal Path - Dijkstra",
            optimalPath.formatPath()
        )

        val fewestHopsPath =
            findFewestHops(
                origin,
                destination
            )

        printResult(
            "Find Fewest Hops Route - BFS",
            fewestHopsPath.formatPath()
        )

        printResult(
            "Verify Hub Link",
            if (verifyHubLink(origin, destination)) {
                "Connected: ${origin.id} -> ${destination.id}"
            } else {
                "No route exists: ${origin.id} -> ${destination.id}"
            }
        )

        val allPairs =
            findAllPairsShortestPath()

        printResult(
            "Find All Pairs Shortest Paths",
            formatAllPairs(allPairs)
        )
    }
// -------------------------------------------------------------------------
// Add Vehicle To Hub
// -------------------------------------------------------------------------

    private fun runAddVehicleToHubDemo() {

        printSection("ADD VEHICLE TO HUB")

        val warehouse = warehouses.firstOrNull()
        val vehicle = vehicles.firstOrNull { it.currentWarehouse.id != warehouse?.id }

        if (warehouse == null || vehicle == null) {
            printResult(
                "Add Vehicle To Hub",
                "No warehouse or vehicle available for demo."
            )
            return
        }

        val oldWarehouseId = vehicle.currentWarehouse.id
        val oldVehicleCount = warehouse.stationedVehicles.size

        addVehicleToHub(warehouse, vehicle)

        val newVehicleCount = warehouse.stationedVehicles.size
        val vehicleAdded = warehouse.stationedVehicles.contains(vehicle)

        printResult(
            "Add Vehicle To Hub",
            """
        Vehicle: ${vehicle.id}
        Capacity: ${formatKg(vehicle.maxCapacityKg)}
        Cost per Km: ${"%.2f".format(vehicle.costPerKm)}
        
        From Warehouse: $oldWarehouseId
        To Warehouse: ${warehouse.id}
        
        Before: $oldVehicleCount vehicles stationed
        After:  $newVehicleCount vehicles stationed
        Success: ${if (vehicleAdded) "✅ Vehicle added successfully" else "❌ Failed to add vehicle"}
        """.trimIndent()
        )
    }

// -------------------------------------------------------------------------
// Reroute Package
// -------------------------------------------------------------------------

    private fun runReroutePackageDemo() {

        printSection("REROUTE PACKAGE")

        val packageItem = packages.firstOrNull()
        val newDestination = warehouses.firstOrNull { it.id != packageItem?.destinationWarehouse?.id }

        if (packageItem == null || newDestination == null) {
            printResult(
                "Reroute Package",
                "No package or alternative destination available."
            )
            return
        }

        val oldDestinationId = packageItem.destinationWarehouse.id

        val updatedPackage = reroutePackage(packageItem, newDestination)

        printResult(
            "Reroute Package",
            """
        Package: ${updatedPackage.id}
        Weight: ${formatKg(updatedPackage.weight)}
        Priority: ${updatedPackage.priority}
        
        Original Destination: $oldDestinationId
        New Destination: ${updatedPackage.destinationWarehouse.id}
        
        Status: ✅ Package rerouted successfully
        """.trimIndent()
        )
    }
    // -------------------------------------------------------------------------
    // Shipment
    // -------------------------------------------------------------------------

    private fun runShipmentQueries() {

        printSection("SHIPMENT")

        val packageItem =
            packages.firstOrNull()

        if (packageItem == null) {
            printResult(
                "Estimate Shipment Delivery",
                "No packages are available."
            )
            return
        }

        val estimatedTime =
            estimateShipmentDelivery(
                packageItem.id
            )

        printResult(
            "Estimate Shipment Delivery",
            estimatedTime?.let {
                """
                Package: ${packageItem.id}
                Estimated Delay: ${"%.2f".format(it)} minutes
                """.trimIndent()
            } ?: "Unable to estimate shipment delivery."
        )
    }

    // -------------------------------------------------------------------------
    // Backhaul
    // -------------------------------------------------------------------------

    private fun runBackhaulQuery() {

        printSection("BACKHAUL")

        val vehicle =
            vehicles.firstOrNull()

        val destination =
            warehouses.firstOrNull {
                it.id != vehicle?.currentWarehouse?.id
            }

        if (vehicle == null || destination == null) {
            printResult(
                "Find Backhaul Opportunity",
                "No suitable vehicle/destination pair is available."
            )
            return
        }

        val opportunity =
            findBackhaulOpportunity(
                vehicle,
                destination
            )

        printResult(
            "Find Backhaul Opportunity",
            opportunity?.formatBackhaul()
                ?: """
                No backhaul opportunity found.
                Vehicle: ${vehicle.id}
                Destination: ${destination.id}
                """.trimIndent()
        )
    }

    // -------------------------------------------------------------------------
    // Cargo Recovery
    // -------------------------------------------------------------------------

    private fun runRecoveryQuery() {

        printSection("CARGO RECOVERY")

        val failedVehicle =
            vehicles.firstOrNull()

        if (failedVehicle == null) {
            printResult(
                "Find Cargo Recovery Plan",
                "No vehicles are available."
            )
            return
        }

        val recoveryPlan =
            findCargoRecoveryPlan(
                failedVehicle
            )

        printResult(
            "Find Cargo Recovery Plan",
            recoveryPlan?.let {
                """
                Failed Vehicle: ${it.failedVehicleId}

                Rescue Assignments:
                ${
                    it.rescueVehicleByPackageId
                        .entries
                        .joinToString("\n") { (packageId, vehicleId) ->
                            "  $packageId -> $vehicleId"
                        }
                }
                """.trimIndent()
            } ?: "No recovery plan is available."
        )
    }

    // -------------------------------------------------------------------------
    // Reporting
    // -------------------------------------------------------------------------

    private fun runReportingQuery() {

        printSection("REPORTING")

        val warehouse =
            warehouses.firstOrNull()

        if (warehouse == null) {
            printResult(
                "Warehouse Report",
                "No warehouses are available."
            )
            return
        }

        val report =
            getWarehouseReport(
                warehouse.id
            )

        printResult(
            "Warehouse Report",
            report?.let {
                """
                Warehouse ID: ${it.warehouseId}
                Packages: ${it.packageCount}
                Package Weight: ${formatKg(it.totalPackageWeight)}
                Fleet Capacity: ${formatKg(it.totalVehicleCapacity)}
                """.trimIndent()
            } ?: "Report could not be generated."
        )
    }

    // -------------------------------------------------------------------------
    // Sub-Task 3 + 4
    // -------------------------------------------------------------------------

    private fun runTreeDemo() {

        printSection("HIERARCHICAL HUB TREE")

        val warehouse =
            warehouses.lastOrNull()

        if (warehouse == null) {
            printResult(
                "Trace Hub Lineage",
                "No warehouses are available."
            )
            return
        }

        val lineage =
            traceHubLineage(warehouse)

        printResult(
            "Trace Hub Lineage",
            if (lineage.isEmpty()) {
                "No lineage found for ${warehouse.id}."
            } else {
                lineage.joinToString(
                    separator = " -> "
                ) { it.id }
            }
        )

        runBalancedTreePerformanceDemo()
    }

    private fun runBalancedTreePerformanceDemo() {

        printSubSection("BST vs AVL PERFORMANCE")

        val trackingIds =
            PackageTrackingIdGenerator()
                .generate(TRACKING_ID_COUNT)

        val bst =
            BST<String>()

        val avl =
            AVLTree<String>()

        trackingIds.forEach { trackingId ->
            bst.insert(trackingId)
            avl.insert(trackingId)
        }

        val targetIds =
            listOf(
                "PKG-000001",
                "PKG-000500",
                "PKG-001000"
            )

        targetIds.forEach { trackingId ->

            val bstSteps =
                bst.search(trackingId)

            val avlSteps =
                avl.search(trackingId)

            println(
                """
                Tracking ID: $trackingId
                  BST steps: $bstSteps
                  AVL steps: $avlSteps
                """.trimIndent()
            )
        }

        println(
            """
            Generated tracking IDs: ${trackingIds.size}
            BST: sequential insertion demonstrates degraded O(N) search.
            AVL: balancing maintains O(log N) search behaviour.
            """.trimIndent()
        )
    }

    // -------------------------------------------------------------------------
    // Sub-Task 5
    // -------------------------------------------------------------------------

    private fun runCommandPatternDemo() {

        printSection("COMMAND PATTERN - DISPATCH PANEL")

        val warehouse =
            warehouses.firstOrNull()

        if (warehouse == null) {
            printResult(
                "Command Pattern",
                "No warehouses are available."
            )
            return
        }

        val packageItem =
            packages.firstOrNull {
                !warehouse.containsPackage(it)
            }

        if (packageItem != null) {

            val assignCommand =
                AssignPackageToQueueCommand(
                    warehouse = warehouse,
                    packageItem = packageItem,
                    assignPackageToQueue = assignPackageToQueue
                )

            val before =
                warehouse.cargoQueue.size

            commandInvoker.execute(
                assignCommand
            )

            val after =
                warehouse.cargoQueue.size

            println(
                """
                AssignPackageToQueueCommand
                  Package: ${packageItem.id}
                  Before queue size: $before
                  After queue size:  $after
                  Can Undo: ${commandInvoker.canUndo()}
                """.trimIndent()
            )

            commandInvoker.undo()

            println(
                """
                Undo
                  Queue size after undo: ${warehouse.cargoQueue.size}
                  Can Undo: ${commandInvoker.canUndo()}
                """.trimIndent()
            )
        } else {
            println(
                "No package is available for assignment demo."
            )
        }

        runDispatchCommandDemo()
    }

    private fun runDispatchCommandDemo() {

        printSubSection("DISPATCH COMMAND")

        val warehouse =
            warehouses.firstOrNull()

        if (warehouse == null) {
            println("No warehouse available.")
            return
        }

        val vehicle =
            warehouse.stationedVehicles
                .firstOrNull { vehicle ->
                    packages.any {
                        warehouse.containsPackage(it) &&
                                vehicle.canCarryWeight(it.weight)
                    }
                }

        if (vehicle == null) {
            println(
                "No vehicle/package pair satisfies dispatch requirements."
            )
            return
        }

        val packageItem =
            packages.first {
                warehouse.containsPackage(it) &&
                        vehicle.canCarryWeight(it.weight)
            }
        val packageList = listOf(packageItem)
        val dispatchCommand =
            DispatchVehicleCommand(
                dispatchVehicleUseCase = dispatchVehicle,
                packages = packageList,
                vehicle = vehicle,
                warehouse = warehouse
            )

        val packagesBefore =
            warehouse.cargoQueue.size

        val vehiclesBefore =
            warehouse.stationedVehicles.size

        val executed =
            runCatching {
                commandInvoker.execute(
                    dispatchCommand
                )
            }.getOrElse {
                println(
                    "Dispatch command failed: ${it.message}"
                )
                false
            }

        if (!executed) {
            return
        }

        println(
            """
            DispatchVehicleCommand
              Vehicle: ${vehicle.id}
              Package: ${packageItem.id}

              Before:
                Packages: $packagesBefore
                Vehicles: $vehiclesBefore

              After Execute:
                Packages: ${warehouse.cargoQueue.size}
                Vehicles: ${warehouse.stationedVehicles.size}

              History available: ${commandInvoker.canUndo()}
            """.trimIndent()
        )

        val undone =
            commandInvoker.undo()

        println(
            """
            Global Undo
              Success: $undone

              After Undo:
                Packages: ${warehouse.cargoQueue.size}
                Vehicles: ${warehouse.stationedVehicles.size}

              State restored:
                Packages restored: ${
                warehouse.cargoQueue.size == packagesBefore
            }
                Vehicle restored: ${
                warehouse.stationedVehicles.size == vehiclesBefore
            }
            """.trimIndent()
        )
    }

    // -------------------------------------------------------------------------
    // Formatting
    // -------------------------------------------------------------------------

    private fun printHeader() {

        println()
        println("=".repeat(CONSOLE_WIDTH))
        println("                 QUANTUM LOGISTICS")
        println("              USE CASE DEMONSTRATION")
        println("=".repeat(CONSOLE_WIDTH))
        println()
    }

    private fun printFooter() {

        println()
        println("=".repeat(CONSOLE_WIDTH))
        println("                 DEMO COMPLETED")
        println("=".repeat(CONSOLE_WIDTH))
    }

    private fun printSection(title: String) {

        println()
        println("─".repeat(CONSOLE_WIDTH))
        println(title)
        println("─".repeat(CONSOLE_WIDTH))
    }

    private fun printSubSection(title: String) {

        println()
        println("[$title]")
        println(".".repeat(title.length + 2))
    }

    private fun printResult(
        operation: String,
        result: String
    ) {

        println()
        println("▶ $operation")
        println(result)
    }

    private fun List<Warehouse>?.formatPath(): String =
        this?.joinToString(
            separator = " -> "
        ) { it.id }
            ?: "No route found."

    private fun formatAllPairs(
        paths: Map<Warehouse, Map<Warehouse, Double>>
    ): String {

        if (paths.isEmpty()) {
            return "No route data available."
        }

        val entries =
            paths
                .flatMap { (origin, destinations) ->
                    destinations
                        .map { (destination, distance) ->
                            origin.id to
                                    destination.id to
                                    distance
                        }
                }
                .filter { (_, pair) ->
                    pair.isFinite()
                }
                .take(DISPLAYED_PAIRS_LIMIT)

        return buildString {
            appendLine("Computed pairs: ${paths.values.sumOf { it.size }}")
            appendLine("Showing first ${entries.size} reachable pairs:")

            entries.forEach { entry ->

                val origin =
                    entry.first.first

                val destination =
                    entry.first.second

                val distance =
                    entry.second

                appendLine(
                    "  $origin -> $destination : " +
                            "${"%.2f".format(distance)} km"
                )
            }
        }.trimEnd()
    }

    private fun List<Package>.formatPackages(): String =
        if (this.isEmpty()) {
            "No packages found."
        } else {
            this.joinToString("\n") {
                "  ${it.id} | ${formatKg(it.weight)} | " +
                        "${it.originWarehouse.id} -> " +
                        it.destinationWarehouse.id +
                        " | ${it.priority}"
            }
        }

    private fun List<Vehicle>.formatVehicles(): String =
        if (this.isEmpty()) {
            "No suitable vehicles found."
        } else {
            this.joinToString("\n") {
                "  ${it.id} | capacity=${formatKg(it.maxCapacityKg)} | " +
                        "cost/km=${"%.2f".format(it.costPerKm)}"
            }
        }

    private fun Vehicle.formatVehicle(): String =
        """
        Vehicle: $id
        Capacity: ${formatKg(maxCapacityKg)}
        Cost per Km: ${"%.2f".format(costPerKm)}
        Current Warehouse: ${currentWarehouse.id}
        """.trimIndent()

    private fun BackhaulOpportunity.formatBackhaul(): String =
        """
        Vehicle: $vehicleId
        Outbound: $outboundWarehouseId
        Return: $returnWarehouseId
        Selected Packages: ${packages.size}
        Total Cargo Weight: ${formatKg(totalCargoWeightKg)}
        Remaining Capacity: ${formatKg(remainingCapacityKg)}

        Packages:
        ${
            packages.joinToString("\n") {
                "  ${it.id} | ${formatKg(it.weight)} | " +
                        "${it.originWarehouse.id} -> " +
                        it.destinationWarehouse.id
            }
        }
        """.trimIndent()

    private fun formatKg(
        value: Double
    ): String =
        "${"%.2f".format(value)} kg"

    private fun formatPercent(
        value: Double
    ): String =
        "${"%.2f".format(value * PERCENTAGE_MULTIPLIER)}%"
}