package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.routing.dijkstra.DijkstraRouter
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.FakeRouteRepository
import org.bytebloom.logiroute.presentation.FakeWarehouseRepository
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.route
import org.bytebloom.logiroute.presentation.warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RoutePresenterTest {

    private val ramallah = warehouse("WH-001", name = "Ramallah")
    private val nablus = warehouse("WH-002", name = "Nablus")
    private val jenin = warehouse("WH-003", name = "Jenin")
    private val hebron = warehouse("WH-004", name = "Hebron")

    // Ramallah -> Nablus (40 km) -> Jenin (25 km); Hebron is unreachable; roads are one-way.
    private val warehouses = listOf(ramallah, nablus, jenin, hebron)
    private val routes = listOf(
        route("RT-001", ramallah, nablus, 40.0),
        route("RT-002", nablus, jenin, 25.0)
    )

    private fun presenter(
        scope: CoroutineScope,
        warehouseRepository: FakeWarehouseRepository = FakeWarehouseRepository(warehouses),
        routeRepository: FakeRouteRepository = FakeRouteRepository(routes)
    ) = RoutePresenter(
        getAllWarehouses = GetAllWarehousesUseCase(warehouseRepository),
        getAllRoutes = GetAllRoutesUseCase(routeRepository),
        pathFinderFor = { graph -> FindOptimalPathUseCase(DijkstraRouter(graph)) },
        parentScope = scope,
        computeDispatcher = Dispatchers.Unconfined
    )

    private suspend fun RoutePresenter.loaded(): RoutePresenter {
        load()
        state.first { it.status == RouteStatus.READY }
        return this
    }

    private suspend fun RoutePresenter.result(from: String, to: String): RouteState {
        calculate(from, to)
        return state.first { it.status in SETTLED }
    }

    @Test
    fun `lists the warehouses once loaded`() = runTest {
        // Given
        val presenter = presenter(backgroundScope)

        // When
        presenter.load()
        val state = presenter.state.first { it.status == RouteStatus.READY }

        // Then
        assertEquals(listOf("Ramallah", "Nablus", "Jenin", "Hebron"), state.warehouses.map { it.name })
    }

    @Test
    fun `reports routes that point to unknown warehouses instead of failing`() = runTest {
        // Given
        val stranger = warehouse("WH-999", name = "Stranger")
        val presenter = presenter(
            backgroundScope,
            routeRepository = FakeRouteRepository(routes + route("RT-099", stranger, ramallah, 5.0))
        )

        // When
        presenter.load()
        val state = presenter.state.first { it.status == RouteStatus.READY }

        // Then
        assertEquals(listOf("RT-099"), state.skippedRouteIds)
    }

    @Test
    fun `shows calculating right after a calculation is requested`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        presenter.calculate("WH-001", "WH-003")

        // Then
        assertEquals(RouteStatus.CALCULATING, presenter.state.value.status)
    }

    @Test
    fun `finds the shortest path through intermediate warehouses`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val plan = presenter.result("WH-001", "WH-003").plan

        // Then
        assertEquals(listOf("Ramallah", "Nablus", "Jenin"), plan?.stops?.map { it.name })
    }

    @Test
    fun `adds up the distance of every leg`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val plan = presenter.result("WH-001", "WH-003").plan

        // Then
        assertEquals(65.0, plan?.totalDistanceKm)
    }

    @Test
    fun `describes every leg of the journey`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val plan = presenter.result("WH-001", "WH-003").plan

        // Then
        assertEquals(listOf(40.0, 25.0), plan?.legs?.map { it.distanceKm })
    }

    @Test
    fun `reports that no route exists when the destination is unreachable`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val state = presenter.result("WH-001", "WH-004")

        // Then
        assertEquals(RouteStatus.NO_ROUTE, state.status)
    }

    @Test
    fun `respects one-way roads`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val state = presenter.result("WH-003", "WH-001")

        // Then
        assertEquals(RouteStatus.NO_ROUTE, state.status)
    }

    @Test
    fun `accepts ids in any letter case and with surrounding spaces`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val state = presenter.result("  wh-001 ", "wH-002")

        // Then
        assertEquals(RouteStatus.FOUND, state.status)
    }

    @Test
    fun `rejects an unknown origin`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val state = presenter.result("WH-404", "WH-002")

        // Then
        assertEquals(UiError.InvalidInput("Warehouse 'WH-404' was not found."), state.error)
    }

    @Test
    fun `rejects an unknown destination`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val state = presenter.result("WH-001", "WH-404")

        // Then
        assertEquals(UiError.InvalidInput("Warehouse 'WH-404' was not found."), state.error)
    }

    @Test
    fun `refuses to calculate before the data was loaded`() = runTest {
        // Given
        val presenter = presenter(backgroundScope)

        // When
        val state = presenter.result("WH-001", "WH-002")

        // Then
        assertNotNull(state.error)
    }

    @Test
    fun `fails with a network error when loading is impossible`() = runTest {
        // Given
        val presenter = presenter(
            backgroundScope,
            warehouseRepository = FakeWarehouseRepository(failure = NetworkUnavailableException("offline"))
        )

        // When
        presenter.load()
        val state = presenter.state.first { it.status == RouteStatus.FAILED }

        // Then
        assertEquals(UiError.Network("offline"), state.error)
    }

    private companion object {
        val SETTLED = setOf(RouteStatus.FOUND, RouteStatus.NO_ROUTE, RouteStatus.FAILED)
    }
}
