package org.bytebloom.logiroute.di

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.presentation.presenter.ClassificationPresenter
import org.bytebloom.logiroute.ui.screen.ClassificationTerminalUI
import org.bytebloom.logiroute.presentation.presenter.ShortestRoutePresenter
import org.bytebloom.logiroute.ui.screen.ShortestRouteTerminalUI
import org.bytebloom.logiroute.presentation.presenter.TelemetryPresenter
import org.bytebloom.logiroute.ui.screen.TelemetryTerminalUI
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
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
}
