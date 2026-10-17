package org.bytebloom.logiroute.di

import org.bytebloom.logiroute.domain.knapsack.KnapsackCargoOptimizer
import org.bytebloom.logiroute.domain.knn.KNearestNeighborsClassifier
import org.bytebloom.logiroute.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.logiroute.domain.pricing.factory.DecoratorFactory
import org.bytebloom.logiroute.domain.pricing.factory.DefaultDecoratorFactory
import org.bytebloom.logiroute.domain.pricing.factory.DefaultStrategyFactory
import org.bytebloom.logiroute.domain.pricing.factory.StrategyFactory
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.bfs.BidirectionalBreadthFirstRouter
import org.bytebloom.logiroute.domain.routing.dijkstra.DijkstraRouter
import org.bytebloom.logiroute.domain.tree.binary.AVLTree
import org.bytebloom.logiroute.domain.tree.binary.BST
import org.bytebloom.logiroute.domain.tree.hierarchicalHub.HubTree
import org.bytebloom.logiroute.domain.usecase.AnalyzeTreePerformanceUseCase
import org.bytebloom.logiroute.domain.usecase.TraceHubLineageUseCase
import org.bytebloom.logiroute.domain.usecase.commands.AddVehicleToHubUseCase
import org.bytebloom.logiroute.domain.usecase.commands.AssignPackageToCargoQueueUseCase
import org.bytebloom.logiroute.domain.usecase.commands.DispatchVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.commands.ReroutePackageUseCase
import org.bytebloom.logiroute.domain.usecase.crud.packages.CreatePackageUseCase
import org.bytebloom.logiroute.domain.usecase.crud.packages.DeletePackageUseCase
import org.bytebloom.logiroute.domain.usecase.crud.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.crud.packages.GetPackageByIdUseCase
import org.bytebloom.logiroute.domain.usecase.crud.packages.UpdatePackageUseCase
import org.bytebloom.logiroute.domain.usecase.crud.routes.CreateRouteUseCase
import org.bytebloom.logiroute.domain.usecase.crud.routes.DeleteRouteUseCase
import org.bytebloom.logiroute.domain.usecase.crud.routes.GetRouteByIdUseCase
import org.bytebloom.logiroute.domain.usecase.crud.routes.UpdateRouteUseCase
import org.bytebloom.logiroute.domain.usecase.crud.vehicles.CreateVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.crud.vehicles.DeleteVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.crud.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.crud.vehicles.GetVehicleByIdUseCase
import org.bytebloom.logiroute.domain.usecase.crud.vehicles.UpdateVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.crud.warehouses.CreateWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.crud.warehouses.DeleteWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.crud.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.domain.usecase.crud.warehouses.GetWarehouseByIdUseCase
import org.bytebloom.logiroute.domain.usecase.crud.warehouses.UpdateWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.greedy.GreedyFleetDispatchUseCase
import org.bytebloom.logiroute.domain.usecase.queries.FindCheapestSuitableVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.queries.FindPackagesAboveWeightUseCase
import org.bytebloom.logiroute.domain.usecase.queries.FindPackagesByDestinationUseCase
import org.bytebloom.logiroute.domain.usecase.queries.FindPackagesByPriorityUseCase
import org.bytebloom.logiroute.domain.usecase.queries.FindStationedVehiclesByCapacityUseCase
import org.bytebloom.logiroute.domain.usecase.queries.GetWarehouseLoadFactorUseCase
import org.bytebloom.logiroute.domain.usecase.queries.backhaul.FindBackhaulOpportunityUseCase
import org.bytebloom.logiroute.domain.usecase.queries.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.queries.knapsack.OptimizeCargoLoadUseCase
import org.bytebloom.logiroute.domain.usecase.queries.planing.FindCargoRecoveryPlanUseCase
import org.bytebloom.logiroute.domain.usecase.queries.pricing.CalculatePricingUseCase
import org.bytebloom.logiroute.domain.usecase.queries.reporting.GetWarehouseReportUseCase
import org.bytebloom.logiroute.domain.usecase.queries.routing.FindAllPairsShortestPathUseCase
import org.bytebloom.logiroute.domain.usecase.queries.routing.FindFewestHopsRouteUseCase
import org.bytebloom.logiroute.domain.usecase.queries.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.queries.routing.VerifyHubLinkUseCase
import org.bytebloom.logiroute.domain.usecase.queries.shipment.EstimateShipmentDeliveryUseCase
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase

val useCaseModule = module {

    single<StrategyFactory> { DefaultStrategyFactory() }
    single<DecoratorFactory> { DefaultDecoratorFactory() }
    single { KnapsackCargoOptimizer() }
    single { KNearestNeighborsClassifier() }
    single { PackageTrackingIdGenerator() }

    factory { BST<String>() }
    factory { AVLTree<String>() }

    factory { GetAllWarehousesUseCase(get()) }
    factory { CreateWarehouseUseCase(get(), get()) }
    factory { GetWarehouseByIdUseCase(get(), get()) }
    factory { UpdateWarehouseUseCase(get(), get()) }
    factory { DeleteWarehouseUseCase(get(), get()) }

    factory { GetAllPackagesUseCase(get()) }
    factory { CreatePackageUseCase(get(), get()) }
    factory { GetPackageByIdUseCase(get(), get()) }
    factory { UpdatePackageUseCase(get(), get()) }
    factory { DeletePackageUseCase(get(), get()) }

    factory { GetAllVehiclesUseCase(get()) }
    factory { CreateRouteUseCase(get(), get()) }
    factory { GetRouteByIdUseCase(get(), get()) }
    factory { UpdateRouteUseCase(get(), get()) }
    factory { DeleteRouteUseCase(get(), get()) }

    factory { GetAllVehiclesUseCase(get()) }
    factory { CreateVehicleUseCase(get(), get()) }
    factory { GetVehicleByIdUseCase(get(), get()) }
    factory { UpdateVehicleUseCase(get(), get()) }
    factory { DeleteVehicleUseCase(get(), get()) }

    factory { AddVehicleToHubUseCase() }
    factory { AssignPackageToCargoQueueUseCase() }
    factory { DispatchVehicleUseCase() }
    factory { ReroutePackageUseCase() }

    factory { FindPackagesAboveWeightUseCase(get()) }
    factory { FindPackagesByDestinationUseCase(get()) }
    factory { FindPackagesByPriorityUseCase(get()) }
    factory { FindCheapestSuitableVehicleUseCase(get()) }
    factory { FindStationedVehiclesByCapacityUseCase() }

    factory { GetWarehouseLoadFactorUseCase() }
    factory { GetWarehouseReportUseCase(get()) }

    factory { FindBackhaulOpportunityUseCase(get()) }
    factory { FindCargoRecoveryPlanUseCase() }
    factory { CalculatePricingUseCase(get(), get(), get()) }

    factory { GreedyFleetDispatchUseCase() }
    factory { OptimizeCargoLoadUseCase(get()) }
    factory { ClassifyPackageSectorUseCase(get(), get()) }

    factory { (graph: WarehouseGraph) ->
        FindOptimalPathUseCase(
            routeFinder = DijkstraRouter(graph)
        )
    }

    factory { (graph: WarehouseGraph) ->
        FindFewestHopsRouteUseCase(
            routeFinder = BidirectionalBreadthFirstRouter(graph)
        )
    }

    factory { (graph: WarehouseGraph) ->
        VerifyHubLinkUseCase(
            routeFinder = DijkstraRouter(graph)
        )
    }

    factory { (graph: WarehouseGraph) ->
        FindAllPairsShortestPathUseCase(
            routeFinder = DijkstraRouter(graph),
            graph = graph
        )
    }

    factory { (graph: WarehouseGraph) ->
        EstimateShipmentDeliveryUseCase(
            packageRepository = get(),
            routeRepository = get(),
            findOptimalPath = get { parametersOf(graph) }
        )
    }

    factory {
        AnalyzeTreePerformanceUseCase(
            trackingIdGenerator = get(),
            binarySearchTree = get(),
            avlTree = get()
        )
    }

    factory { (hubTree: HubTree) ->
        TraceHubLineageUseCase(
            hubTree = hubTree
        )
    }

    factory {
        TrackActiveFleetUseCase(
            telemetryRepository = get()        )
    }
}