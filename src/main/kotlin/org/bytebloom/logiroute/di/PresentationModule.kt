package org.bytebloom.logiroute.di

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.presentation.presenter.ClassificationPresenter
import org.bytebloom.logiroute.ui.screens.ClassificationTerminalUI
import org.bytebloom.logiroute.presentation.presenter.ShortestRoutePresenter
import org.bytebloom.logiroute.ui.screens.ShortestRouteTerminalUI
import org.bytebloom.logiroute.presentation.presenter.TelemetryPresenter
import org.bytebloom.logiroute.ui.screens.TelemetryTerminalUI
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.tree.hub.HubTree
import org.bytebloom.logiroute.domain.usecase.routing.FindAllPairsShortestPathUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindFewestHopsRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routing.VerifyHubLinkUseCase
import org.bytebloom.logiroute.domain.usecase.shipment.EstimateShipmentDeliveryUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.TraceHubLineageUseCase
import org.bytebloom.logiroute.presentation.presenter.AnalyticsPresenter
import org.bytebloom.logiroute.ui.screens.AnalyticsTerminalUI
import org.bytebloom.logiroute.presentation.presenter.DispatchPresenter
import org.bytebloom.logiroute.ui.screens.DispatchTerminalUI
import org.bytebloom.logiroute.presentation.presenter.FleetPlanningPresenter
import org.bytebloom.logiroute.ui.screens.FleetPlanningTerminalUI
import org.bytebloom.logiroute.presentation.presenter.SystemOverviewPresenter
import org.bytebloom.logiroute.ui.screens.SystemOverviewTerminalUI
import org.bytebloom.logiroute.presentation.presenter.PackagesManagementPresenter
import org.bytebloom.logiroute.ui.screens.PackagesManagementTerminalUI
import org.bytebloom.logiroute.presentation.presenter.PricingPresenter
import org.bytebloom.logiroute.ui.screens.PricingTerminalUI
import org.bytebloom.logiroute.presentation.presenter.RouteAnalysisPresenter
import org.bytebloom.logiroute.ui.screens.RouteAnalysisTerminalUI
import org.bytebloom.logiroute.presentation.presenter.RoutesManagementPresenter
import org.bytebloom.logiroute.ui.screens.RoutesManagementTerminalUI
import org.bytebloom.logiroute.presentation.presenter.ShipmentsPresenter
import org.bytebloom.logiroute.ui.screens.ShipmentsTerminalUI
import org.bytebloom.logiroute.presentation.presenter.VehiclesManagementPresenter
import org.bytebloom.logiroute.ui.screens.VehiclesManagementTerminalUI
import org.bytebloom.logiroute.presentation.presenter.WarehousesManagementPresenter
import org.bytebloom.logiroute.ui.screens.WarehousesManagementTerminalUI
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * Wiring of the TERMINAL application: every screen's UI class and its presenter.
 *
 * It lives in the presentation package because the `di` package is not allowed to know about
 * presentation (see CleanArchitectureTest).
 *
 * An Android app has its own module: the same presenters, built with the Fragment / ViewModel
 * as `view` and a ViewModel scope instead of [uiScope].
 *
 * Each UI class receives a "presenter factory" because the view and the presenter need each other:
 * the UI creates its presenter on first use and passes itself as the view.
 */
fun presentationModule(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope
) = module {

    factory {
        ShortestRouteTerminalUI(terminal, style, uiScope) { view ->
            ShortestRoutePresenter(
                getAllWarehouses = get(),
                getAllRoutes = get(),
                pathFinderFor = { graph -> get<FindOptimalPathUseCase> { parametersOf(graph) } },
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        ClassificationTerminalUI(terminal, style, uiScope) { view ->
            ClassificationPresenter(
                getAllPackages = get(),
                classifyPackage = get(),
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        TelemetryTerminalUI(terminal, style, uiScope) { view ->
            TelemetryPresenter(
                getAllVehicles = get(),
                streamFleetTelemetry = get(),
                trackActiveFleet = get(),
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        SystemOverviewTerminalUI(terminal, style, uiScope) { view ->
            SystemOverviewPresenter(get(), get(), get(), get(), view, uiScope)
        }
    }

    factory {
        WarehousesManagementTerminalUI(terminal, style, uiScope) { view ->
            WarehousesManagementPresenter(
                getAllWarehouses = get(),
                getWarehouseById = get(),
                createWarehouse = get(),
                updateWarehouse = get(),
                deleteWarehouse = get(),
                getWarehouseReport = get(),
                getWarehouseLoadFactor = get(),
                hubTreeBuilder = get(),
                lineageFor = { tree: HubTree -> get<TraceHubLineageUseCase> { parametersOf(tree) } },
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        VehiclesManagementTerminalUI(terminal, style, uiScope) { view ->
            VehiclesManagementPresenter(
                getAllVehicles = get(),
                getVehicleById = get(),
                createVehicle = get(),
                updateVehicle = get(),
                deleteVehicle = get(),
                findStationedVehiclesByCapacity = get(),
                findCheapestSuitableVehicle = get(),
                getAllWarehouses = get(),
                getAllPackages = get(),
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        PackagesManagementTerminalUI(terminal, style, uiScope) { view ->
            PackagesManagementPresenter(
                getAllPackages = get(),
                getPackageById = get(),
                createPackage = get(),
                updatePackage = get(),
                deletePackage = get(),
                findPackagesAboveWeight = get(),
                findPackagesByDestination = get(),
                findPackagesByPriority = get(),
                getAllWarehouses = get(),
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        RoutesManagementTerminalUI(terminal, style, uiScope) { view ->
            RoutesManagementPresenter(
                getAllRoutes = get(),
                getRouteById = get(),
                createRoute = get(),
                updateRoute = get(),
                deleteRoute = get(),
                getAllWarehouses = get(),
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        RouteAnalysisTerminalUI(terminal, style, uiScope) { view ->
            RouteAnalysisPresenter(
                getAllWarehouses = get(),
                getAllRoutes = get(),
                fewestHopsFor = { graph: WarehouseGraph -> get<FindFewestHopsRouteUseCase> { parametersOf(graph) } },
                verifyLinkFor = { graph: WarehouseGraph -> get<VerifyHubLinkUseCase> { parametersOf(graph) } },
                allPairsFor = { graph: WarehouseGraph -> get<FindAllPairsShortestPathUseCase> { parametersOf(graph) } },
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        DispatchTerminalUI(terminal, style, uiScope) { view ->
            DispatchPresenter(
                getAllWarehouses = get(),
                getAllVehicles = get(),
                getAllPackages = get(),
                assignPackageToCargoQueue = get(),
                addVehicleToHub = get(),
                dispatchVehicle = get(),
                dispatchCargo = get(),
                optimizeCargoLoad = get(),
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        FleetPlanningTerminalUI(terminal, style, uiScope) { view ->
            FleetPlanningPresenter(
                getAllWarehouses = get(),
                getAllVehicles = get(),
                greedyFleetDispatch = get(),
                findBackhaulOpportunity = get(),
                findCargoRecoveryPlan = get(),
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        PricingTerminalUI(terminal, style, uiScope) { view ->
            PricingPresenter(getAllPackages = get(), calculatePricing = get(), view = view, parentScope = uiScope)
        }
    }

    factory {
        ShipmentsTerminalUI(terminal, style, uiScope) { view ->
            ShipmentsPresenter(
                getAllPackages = get(),
                getAllWarehouses = get(),
                getAllRoutes = get(),
                advanceShipmentState = get(),
                reroutePackage = get(),
                estimateFor = { graph: WarehouseGraph -> get<EstimateShipmentDeliveryUseCase> { parametersOf(graph) } },
                view = view,
                parentScope = uiScope
            )
        }
    }

    factory {
        AnalyticsTerminalUI(terminal, style, uiScope) { view ->
            AnalyticsPresenter(
                analyzeTreePerformance = get(),
                trackingIds = get(),
                view = view,
                parentScope = uiScope
            )
        }
    }
}