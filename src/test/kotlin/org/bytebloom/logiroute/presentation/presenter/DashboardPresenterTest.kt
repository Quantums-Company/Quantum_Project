package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.FakePackageRepository
import org.bytebloom.logiroute.presentation.FakeRouteRepository
import org.bytebloom.logiroute.presentation.FakeVehicleRepository
import org.bytebloom.logiroute.presentation.FakeWarehouseRepository
import org.bytebloom.logiroute.presentation.model.LoadStatus
import org.bytebloom.logiroute.presentation.model.PriorityUi
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.parcel
import org.bytebloom.logiroute.presentation.route
import org.bytebloom.logiroute.presentation.vehicle
import org.bytebloom.logiroute.presentation.warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DashboardPresenterTest {

    private val ramallah = warehouse("WH-001", name = "Ramallah Hub")
    private val nablus = warehouse("WH-002", name = "Nablus Depot")

    private fun presenter(
        scope: CoroutineScope,
        warehouses: FakeWarehouseRepository = FakeWarehouseRepository(listOf(ramallah, nablus)),
        routes: FakeRouteRepository = FakeRouteRepository(listOf(route("RT-001", ramallah, nablus, 42.5))),
        vehicles: FakeVehicleRepository = FakeVehicleRepository(listOf(vehicle("TRK-001", ramallah))),
        packages: FakePackageRepository = FakePackageRepository(listOf(parcel("PKG-001", ramallah, nablus, 12.0)))
    ) = DashboardPresenter(
        getAllWarehouses = GetAllWarehousesUseCase(warehouses),
        getAllRoutes = GetAllRoutesUseCase(routes),
        getAllVehicles = GetAllVehiclesUseCase(vehicles),
        getAllPackages = GetAllPackagesUseCase(packages),
        parentScope = scope
    )

    @Test
    fun `starts idle with nothing loaded`() = runTest {
        // Given / When
        val presenter = presenter(backgroundScope)

        // Then
        assertEquals(DashboardState(), presenter.state.value)
    }

    @Test
    fun `shows loading right after load is requested`() = runTest {
        // Given
        val presenter = presenter(backgroundScope)

        // When
        presenter.load()

        // Then
        assertEquals(LoadStatus.LOADING, presenter.state.value.status)
    }

    @Test
    fun `loads warehouses as ui models`() = runTest {
        // Given
        val presenter = presenter(backgroundScope)

        // When
        presenter.load()
        val state = presenter.state.first { it.status == LoadStatus.READY }

        // Then
        assertEquals(listOf("Ramallah Hub", "Nablus Depot"), state.warehouses.map { it.name })
    }

    @Test
    fun `loads vehicles with the name of the warehouse they are stationed at`() = runTest {
        // Given
        val presenter = presenter(backgroundScope)

        // When
        presenter.load()
        val state = presenter.state.first { it.status == LoadStatus.READY }

        // Then
        assertEquals("Ramallah Hub", state.vehicles.single().warehouseName)
    }

    @Test
    fun `loads packages with readable origin destination and priority`() = runTest {
        // Given
        val presenter = presenter(backgroundScope)

        // When
        presenter.load()
        val parcel = presenter.state.first { it.status == LoadStatus.READY }.packages.single()

        // Then
        assertEquals(Triple("Ramallah Hub", "Nablus Depot", PriorityUi.STANDARD), Triple(parcel.originName, parcel.destinationName, parcel.priority))
    }

    @Test
    fun `loads routes with their distance`() = runTest {
        // Given
        val presenter = presenter(backgroundScope)

        // When
        presenter.load()
        val state = presenter.state.first { it.status == LoadStatus.READY }

        // Then
        assertEquals(42.5, state.routes.single().distanceKm)
    }

    @Test
    fun `fails with a network error when the remote data cannot be reached`() = runTest {
        // Given
        val presenter = presenter(
            backgroundScope,
            warehouses = FakeWarehouseRepository(failure = NetworkUnavailableException("offline"))
        )

        // When
        presenter.load()
        val state = presenter.state.first { it.status == LoadStatus.FAILED }

        // Then
        assertEquals(UiError.Network("offline"), state.error)
    }

    @Test
    fun `fails as a whole when only one of the parallel loads fails`() = runTest {
        // Given
        val presenter = presenter(
            backgroundScope,
            vehicles = FakeVehicleRepository(failure = IllegalStateException("vehicles down"))
        )

        // When
        presenter.load()
        val state = presenter.state.first { it.status == LoadStatus.FAILED }

        // Then
        assertEquals(UiError.Unexpected("vehicles down"), state.error)
    }

    @Test
    fun `clears the previous error when loading again`() = runTest {
        // Given
        val presenter = presenter(
            backgroundScope,
            warehouses = FakeWarehouseRepository(failure = NetworkUnavailableException("offline"))
        )
        presenter.load()
        presenter.state.first { it.status == LoadStatus.FAILED }

        // When
        presenter.load()

        // Then
        assertNull(presenter.state.value.error)
    }
}
