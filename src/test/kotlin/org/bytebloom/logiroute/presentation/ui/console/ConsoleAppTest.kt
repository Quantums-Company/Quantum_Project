package org.bytebloom.logiroute.presentation.ui.console

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.knn.KNearestNeighborsClassifier
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.routing.dijkstra.DijkstraRouter
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.StreamFleetTelemetryUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.FakePackageRepository
import org.bytebloom.logiroute.presentation.FakeRouteRepository
import org.bytebloom.logiroute.presentation.FakeTelemetryRepository
import org.bytebloom.logiroute.presentation.FakeTrainingCargoRepository
import org.bytebloom.logiroute.presentation.FakeVehicleRepository
import org.bytebloom.logiroute.presentation.FakeWarehouseRepository
import org.bytebloom.logiroute.presentation.endlessReadings
import org.bytebloom.logiroute.presentation.parcel
import org.bytebloom.logiroute.presentation.presenter.AppPresenters
import org.bytebloom.logiroute.presentation.presenter.ClassificationPresenter
import org.bytebloom.logiroute.presentation.presenter.DashboardPresenter
import org.bytebloom.logiroute.presentation.presenter.RoutePresenter
import org.bytebloom.logiroute.presentation.presenter.TelemetryPresenter
import org.bytebloom.logiroute.presentation.presenter.TelemetryStatus
import org.bytebloom.logiroute.presentation.reading
import org.bytebloom.logiroute.presentation.route
import org.bytebloom.logiroute.presentation.trainingRecord
import org.bytebloom.logiroute.presentation.vehicle
import org.bytebloom.logiroute.presentation.warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConsoleAppTest {

    private val style = ConsoleStyle(colors = false, unicode = true)

    private val ramallah = warehouse("WH-001", name = "Ramallah Hub")
    private val nablus = warehouse("WH-002", name = "Nablus Depot")
    private val jenin = warehouse("WH-003", name = "Jenin Yard")

    private val warehouses = FakeWarehouseRepository(listOf(ramallah, nablus, jenin))
    private val routes = FakeRouteRepository(
        listOf(route("RT-001", ramallah, nablus, 40.0), route("RT-002", nablus, jenin, 25.0))
    )
    private val vehicles = FakeVehicleRepository(listOf(vehicle("TRK-001", ramallah)))
    private val packages = FakePackageRepository(listOf(parcel("PKG-001", ramallah, jenin)))
    private val training = FakeTrainingCargoRepository(List(5) { trainingRecord(Region.NORTH, 31.9 + it * 0.001, 35.2) })

    private fun presenters(
        scope: CoroutineScope,
        telemetry: FakeTelemetryRepository = FakeTelemetryRepository(),
        warehouseRepository: FakeWarehouseRepository = warehouses
    ) = AppPresenters(
        dashboard = DashboardPresenter(
            GetAllWarehousesUseCase(warehouseRepository), GetAllRoutesUseCase(routes),
            GetAllVehiclesUseCase(vehicles), GetAllPackagesUseCase(packages), scope
        ),
        route = RoutePresenter(
            GetAllWarehousesUseCase(warehouseRepository), GetAllRoutesUseCase(routes),
            { graph -> FindOptimalPathUseCase(DijkstraRouter(graph)) }, scope, Dispatchers.Unconfined
        ),
        classification = ClassificationPresenter(
            GetAllPackagesUseCase(packages),
            ClassifyPackageSectorUseCase(training, KNearestNeighborsClassifier()), scope, Dispatchers.Unconfined
        ),
        telemetry = TelemetryPresenter(
            GetAllVehiclesUseCase(vehicles), StreamFleetTelemetryUseCase(telemetry),
            TrackActiveFleetUseCase(telemetry), scope
        )
    )

    /** Types [lines] and runs the app until it returns; returns everything it printed. */
    private suspend fun runWith(
        scope: CoroutineScope,
        vararg lines: String,
        telemetry: FakeTelemetryRepository = FakeTelemetryRepository(),
        warehouseRepository: FakeWarehouseRepository = warehouses
    ): String {
        val terminal = FakeTerminal()
        lines.forEach(terminal::type)
        ConsoleApp(terminal, style, presenters(scope, telemetry, warehouseRepository)).run()
        return terminal.output
    }

    @Test
    fun `greets the user with a banner and a menu`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "0")

        // Then
        assertTrue("LogiRoute" in output && "Plan the shortest route" in output)
    }

    @Test
    fun `says goodbye when the user chooses exit`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "0")

        // Then
        assertTrue("Goodbye" in output)
    }

    @Test
    fun `leaves cleanly when the input is closed`() = runTest {
        // Given
        val terminal = FakeTerminal()
        terminal.endInput()

        // When
        ConsoleApp(terminal, style, presenters(backgroundScope)).run()

        // Then
        assertTrue("Goodbye" in terminal.output)
    }

    @Test
    fun `explains an unknown option and keeps running`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "banana", "0")

        // Then
        assertTrue("'banana' is not an option" in output && "Goodbye" in output)
    }

    @Test
    fun `shows the system overview`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "1", "0")

        // Then
        assertTrue("System overview" in output && "Ramallah Hub" in output && "TRK-001" in output && "PKG-001" in output)
    }

    @Test
    fun `explains that the overview could not be loaded`() = runTest {
        // Given
        val offline = FakeWarehouseRepository(failure = NetworkUnavailableException("Check your internet connection."))

        // When
        val output = runWith(backgroundScope, "1", "0", warehouseRepository = offline)

        // Then
        assertTrue("No connection" in output && "Check your internet connection." in output)
    }

    @Test
    fun `plans a route chosen by list number`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "2", "1", "3", "0")

        // Then
        assertTrue("Shortest route" in output && "65.00" in output && "Ramallah Hub" in output && "Jenin Yard" in output)
    }

    @Test
    fun `plans a route chosen by warehouse id`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "route", "wh-001", "WH-002", "0")

        // Then
        assertTrue("Shortest route" in output && "40.00" in output)
    }

    @Test
    fun `says so when no route exists`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "2", "3", "1", "0")

        // Then
        assertTrue("No route exists between Jenin Yard and Ramallah Hub" in output)
    }

    @Test
    fun `asks again when the warehouse is not in the list`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "2", "zzz", "1", "3", "0")

        // Then
        assertTrue("'zzz' is not in the list" in output && "Shortest route" in output)
    }

    @Test
    fun `cancels route planning when the user just presses enter`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "2", "", "0")

        // Then
        assertFalse("Shortest route" in output)
    }

    @Test
    fun `classifies a package`() = runTest {
        // Given / When
        val output = runWith(backgroundScope, "3", "1", "0")

        // Then
        assertTrue("Predicted region" in output && "NORTH" in output && "PKG-001" in output)
    }

    @Test
    fun `streams live telemetry until the user presses enter`() = runTest {
        // Given
        val telemetry = FakeTelemetryRepository { endlessReadings(30, reading("TRK-001")) }
        val terminal = FakeTerminal()
        val presenters = presenters(backgroundScope, telemetry)
        terminal.type("4")
        backgroundScope.launch {
            delay(200)
            terminal.type("")
            terminal.type("0")
        }

        // When
        ConsoleApp(terminal, style, presenters).run()

        // Then
        assertTrue("Live fleet telemetry" in terminal.output && "#0001" in terminal.output && "Telemetry stopped" in terminal.output)
    }

    @Test
    fun `prints a delay alert when a vehicle crawls`() = runTest {
        // Given
        val telemetry = FakeTelemetryRepository { endlessReadings(30, reading("TRK-001", speedKph = 0.0)) }
        val terminal = FakeTerminal()
        val presenters = presenters(backgroundScope, telemetry)
        terminal.type("4")
        backgroundScope.launch {
            delay(200)
            terminal.type("")
            terminal.type("0")
        }

        // When
        ConsoleApp(terminal, style, presenters).run()

        // Then
        assertTrue("DELAY  TRK-001 slowed to 0 km/h" in terminal.output)
    }

    @Test
    fun `stops the telemetry stream when the input is closed while streaming`() = runTest {
        // Given
        val telemetry = FakeTelemetryRepository { endlessReadings(30, reading("TRK-001")) }
        val terminal = FakeTerminal()
        val presenters = presenters(backgroundScope, telemetry)
        terminal.type("4")
        backgroundScope.launch {
            delay(200)
            terminal.endInput()
        }

        // When
        ConsoleApp(terminal, style, presenters).run()

        // Then
        assertEquals(TelemetryStatus.STOPPED, presenters.telemetry.state.value.status)
    }

    @Test
    fun `leaves no presenter work running after they are closed`() = runTest {
        // Given
        val telemetry = FakeTelemetryRepository { endlessReadings(30, reading("TRK-001")) }
        val terminal = FakeTerminal()
        val presenters = presenters(backgroundScope, telemetry)
        terminal.type("4")
        backgroundScope.launch {
            delay(100)
            terminal.endInput()
        }
        ConsoleApp(terminal, style, presenters).run()

        // When
        presenters.close()
        delay(100)

        // Then
        assertEquals(TelemetryStatus.STOPPED, presenters.telemetry.state.value.status)
    }
}
