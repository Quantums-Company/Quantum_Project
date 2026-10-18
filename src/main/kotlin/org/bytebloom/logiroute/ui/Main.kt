package org.bytebloom.logiroute.ui

import kotlinx.coroutines.runBlocking
import org.bytebloom.logiroute.di.localDataModule
import org.bytebloom.logiroute.di.networkModule
import org.bytebloom.logiroute.di.repositoryModule
import org.bytebloom.logiroute.di.telemetryModule
import org.bytebloom.logiroute.di.useCaseModule
import org.bytebloom.logiroute.di.validatorModule
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.AssignPackageToCargoQueueUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.StreamFleetTelemetryUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.ClassificationPresenter
import org.bytebloom.logiroute.presentation.CommandPatternPresenter
import org.bytebloom.logiroute.presentation.DashboardPresenter
import org.bytebloom.logiroute.presentation.PackagesManagementPresenter
import org.bytebloom.logiroute.presentation.RoutesManagementPresenter
import org.bytebloom.logiroute.presentation.TelemetryPresenter
import org.bytebloom.logiroute.presentation.VehiclesManagementPresenter
import org.bytebloom.logiroute.ui.console.ConsoleApp
import org.bytebloom.logiroute.ui.console.ConsoleClassificationUI
import org.bytebloom.logiroute.ui.console.ConsoleCommandPatternUI
import org.bytebloom.logiroute.ui.console.ConsoleDashboardUI
import org.bytebloom.logiroute.ui.console.ConsolePackagesManagementUI
import org.bytebloom.logiroute.ui.console.ConsoleRoutesManagementUI
import org.bytebloom.logiroute.ui.console.ConsoleTelemetryUI
import org.bytebloom.logiroute.ui.console.ConsoleVehiclesManagementUI
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf

/** Composition root: wires use cases to presenters and presenters to the selected UI implementation. */
fun main() {
    val koinApp = startKoin {
        modules(
            networkModule,
            localDataModule,
            telemetryModule,
            repositoryModule,
            validatorModule,
            useCaseModule
        )
    }

    try {
        val koin = koinApp.koin

        // Screen contracts are the boundary between each Presenter and its UI implementation.
        val dashboardPresenter = DashboardPresenter(
            getAllWarehouses = koin.get<GetAllWarehousesUseCase>(),
            getAllRoutes = koin.get<GetAllRoutesUseCase>(),
            getAllPackages = koin.get<GetAllPackagesUseCase>(),
            getAllVehicles = koin.get<GetAllVehiclesUseCase>(),
            screen = ConsoleDashboardUI()
        )
        val packagesPresenter = PackagesManagementPresenter(
            getAllPackages = koin.get<GetAllPackagesUseCase>(),
            screen = ConsolePackagesManagementUI()
        )
        val vehiclesPresenter = VehiclesManagementPresenter(
            getAllVehicles = koin.get<GetAllVehiclesUseCase>(),
            screen = ConsoleVehiclesManagementUI()
        )
        val routesPresenter = RoutesManagementPresenter(
            getAllWarehouses = koin.get<GetAllWarehousesUseCase>(),
            getAllRoutes = koin.get<GetAllRoutesUseCase>(),
            findOptimalPath = { graph ->
                koin.get<FindOptimalPathUseCase> { parametersOf(graph) }
            },
            screen = ConsoleRoutesManagementUI()
        )
        val classificationPresenter = ClassificationPresenter(
            getAllPackages = koin.get<GetAllPackagesUseCase>(),
            classifyPackage = koin.get<ClassifyPackageSectorUseCase>(),
            screen = ConsoleClassificationUI()
        )
        val telemetryPresenter = TelemetryPresenter(
            getAllVehicles = koin.get<GetAllVehiclesUseCase>(),
            streamFleetTelemetry = koin.get<StreamFleetTelemetryUseCase>(),
            screen = ConsoleTelemetryUI()
        )
        val commandPatternPresenter = CommandPatternPresenter(
            getAllWarehouses = koin.get<GetAllWarehousesUseCase>(),
            getAllPackages = koin.get<GetAllPackagesUseCase>(),
            getAllVehicles = koin.get<GetAllVehiclesUseCase>(),
            assignPackage = koin.get<AssignPackageToCargoQueueUseCase>(),
            dispatchVehicle = koin.get<DispatchVehicleUseCase>(),
            screen = ConsoleCommandPatternUI()
        )

        runBlocking {
            ConsoleApp(
                commandPatternPresenter = commandPatternPresenter,
                dashboardPresenter = dashboardPresenter,
                packagesPresenter = packagesPresenter,
                vehiclesPresenter = vehiclesPresenter,
                routesPresenter = routesPresenter,
                classificationPresenter = classificationPresenter,
                telemetryPresenter = telemetryPresenter
            ).run()
        }
    } finally {
        koinApp.close()
    }
}
