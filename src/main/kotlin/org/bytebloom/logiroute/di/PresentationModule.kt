package org.bytebloom.logiroute.di

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.presentation.presenter.AppPresenters
import org.bytebloom.logiroute.presentation.presenter.ClassificationPresenter
import org.bytebloom.logiroute.presentation.presenter.DashboardPresenter
import org.bytebloom.logiroute.presentation.presenter.RoutePresenter
import org.bytebloom.logiroute.presentation.presenter.TelemetryPresenter
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * Wiring of the presenters. It lives in the presentation package because the `di` package
 * is not allowed to know about presentation (see CleanArchitectureTest).
 * Every presenter receives the CoroutineScope of whoever owns the screen:
 * the console's application scope today, a ViewModel's `viewModelScope` on Android tomorrow.
 */
val presentationModule = module {

    factory { (scope: CoroutineScope) ->
        DashboardPresenter(
            getAllWarehouses = get(),
            getAllRoutes = get(),
            getAllVehicles = get(),
            getAllPackages = get(),
            parentScope = scope
        )
    }

    factory { (scope: CoroutineScope) ->
        RoutePresenter(
            getAllWarehouses = get(),
            getAllRoutes = get(),
            pathFinderFor = { graph -> get<FindOptimalPathUseCase> { parametersOf(graph) } },
            parentScope = scope
        )
    }

    factory { (scope: CoroutineScope) ->
        ClassificationPresenter(
            getAllPackages = get(),
            classifyPackage = get(),
            parentScope = scope
        )
    }

    factory { (scope: CoroutineScope) ->
        TelemetryPresenter(
            getAllVehicles = get(),
            streamFleetTelemetry = get(),
            trackActiveFleet = get(),
            parentScope = scope
        )
    }

    factory { (scope: CoroutineScope) ->
        AppPresenters(
            dashboard = get { parametersOf(scope) },
            route = get { parametersOf(scope) },
            classification = get { parametersOf(scope) },
            telemetry = get { parametersOf(scope) }
        )
    }
}
