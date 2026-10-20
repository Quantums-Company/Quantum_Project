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
import org.bytebloom.logiroute.domain.usecase.dispatch.AddVehicleToHubUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.AssignPackageToCargoQueueUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.ReroutePackageUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.GreedyFleetDispatchUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.FindBackhaulOpportunityUseCase
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.OptimizeCargoLoadUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.FindCargoRecoveryPlanUseCase
import org.bytebloom.logiroute.domain.usecase.packages.CreatePackageUseCase
import org.bytebloom.logiroute.domain.usecase.packages.DeletePackageUseCase
import org.bytebloom.logiroute.domain.usecase.packages.FindPackagesAboveWeightUseCase
import org.bytebloom.logiroute.domain.usecase.packages.FindPackagesByDestinationUseCase
import org.bytebloom.logiroute.domain.usecase.packages.FindPackagesByPriorityUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetPackageByIdUseCase
import org.bytebloom.logiroute.domain.usecase.packages.UpdatePackageUseCase
import org.bytebloom.logiroute.domain.usecase.pricing.CalculatePricingUseCase
import org.bytebloom.logiroute.domain.usecase.routes.CreateRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routes.DeleteRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetRouteByIdUseCase
import org.bytebloom.logiroute.domain.usecase.routes.UpdateRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindAllPairsShortestPathUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindFewestHopsRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.routing.VerifyHubLinkUseCase
import org.bytebloom.logiroute.domain.usecase.shipment.EstimateShipmentDeliveryUseCase
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.CreateVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.DeleteVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.FindCheapestSuitableVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.FindStationedVehiclesByCapacityUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetVehicleByIdUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.UpdateVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.CreateWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.DeleteWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseByIdUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseLoadFactorUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseReportUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.TraceHubLineageUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.UpdateWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.StreamFleetTelemetryUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.SearchTelemetryUseCase

val useCaseModule = module {

    single<StrategyFactory> { DefaultStrategyFactory() }
    single<DecoratorFactory> { DefaultDecoratorFactory() }
    single { KnapsackCargoOptimizer() }
    single { KNearestNeighborsClassifier() }
    single { PackageTrackingIdGenerator() }

    factory { BST<String>() }
    factory { AVLTree<String>() }

    factory { GetAllWarehousesUseCase(get()) }
    factory {
        CreateWarehouseUseCase(
            get(),
            get()
        )
    }
    factory {
        GetWarehouseByIdUseCase(
            get(),
            get()
        )
    }
    factory {
        UpdateWarehouseUseCase(
            get(),
            get()
        )
    }
    factory {
        DeleteWarehouseUseCase(
            get(),
            get()
        )
    }

    factory { GetAllPackagesUseCase(get()) }
    factory { CreatePackageUseCase(get(), get()) }
    factory { GetPackageByIdUseCase(get(), get()) }
    factory { UpdatePackageUseCase(get(), get()) }
    factory { DeletePackageUseCase(get(), get()) }

    factory { GetAllRoutesUseCase(get()) }
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

    factory { StreamFleetTelemetryUseCase(get()) }
    factory { SearchTelemetryUseCase(get()) }
}