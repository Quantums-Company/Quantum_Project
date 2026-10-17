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
import org.bytebloom.logiroute.domain.tree.hub.HubTree
import org.bytebloom.logiroute.domain.usecase.analytics.AnalyzeTreePerformanceUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.TraceHubLineageUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.AddVehicleToHubUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.AssignPackageToCargoQueueUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.ReroutePackageUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.GreedyFleetDispatchUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.FindBackhaulOpportunityUseCase
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.OptimizeCargoLoadUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.FindCargoRecoveryPlanUseCase
import org.bytebloom.logiroute.domain.usecase.pricing.CalculatePricingUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseReportUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindAllPairsShortestPathUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindFewestHopsRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.routing.VerifyHubLinkUseCase
import org.bytebloom.logiroute.domain.usecase.shipment.EstimateShipmentDeliveryUseCase
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

    factory { org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase(get()) }
    factory {
        org.bytebloom.logiroute.domain.usecase.warehouses.CreateWarehouseUseCase(
            get(),
            get()
        )
    }
    factory {
        org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseByIdUseCase(
            get(),
            get()
        )
    }
    factory {
        org.bytebloom.logiroute.domain.usecase.warehouses.UpdateWarehouseUseCase(
            get(),
            get()
        )
    }
    factory {
        org.bytebloom.logiroute.domain.usecase.warehouses.DeleteWarehouseUseCase(
            get(),
            get()
        )
    }

    factory { org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase(get()) }
    factory { org.bytebloom.logiroute.domain.usecase.packages.CreatePackageUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.packages.GetPackageByIdUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.packages.UpdatePackageUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.packages.DeletePackageUseCase(get(), get()) }

    factory { org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase(get()) }
    factory { org.bytebloom.logiroute.domain.usecase.routes.CreateRouteUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.routes.GetRouteByIdUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.routes.UpdateRouteUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.routes.DeleteRouteUseCase(get(), get()) }

    factory { org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase(get()) }
    factory { org.bytebloom.logiroute.domain.usecase.vehicles.CreateVehicleUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.vehicles.GetVehicleByIdUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.vehicles.UpdateVehicleUseCase(get(), get()) }
    factory { org.bytebloom.logiroute.domain.usecase.vehicles.DeleteVehicleUseCase(get(), get()) }

    factory { AddVehicleToHubUseCase() }
    factory { AssignPackageToCargoQueueUseCase() }
    factory { DispatchVehicleUseCase() }
    factory { ReroutePackageUseCase() }

    factory { org.bytebloom.logiroute.domain.usecase.packages.FindPackagesAboveWeightUseCase(get()) }
    factory { org.bytebloom.logiroute.domain.usecase.packages.FindPackagesByDestinationUseCase(get()) }
    factory { org.bytebloom.logiroute.domain.usecase.packages.FindPackagesByPriorityUseCase(get()) }
    factory { org.bytebloom.logiroute.domain.usecase.vehicles.FindCheapestSuitableVehicleUseCase(get()) }
    factory { org.bytebloom.logiroute.domain.usecase.vehicles.FindStationedVehiclesByCapacityUseCase() }

    factory { org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseLoadFactorUseCase() }
    factory { org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseReportUseCase(get()) }

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
        org.bytebloom.logiroute.domain.usecase.warehouses.TraceHubLineageUseCase(
            hubTree = hubTree
        )
    }

    factory {
        TrackActiveFleetUseCase(
            telemetryRepository = get()        )
    }
}