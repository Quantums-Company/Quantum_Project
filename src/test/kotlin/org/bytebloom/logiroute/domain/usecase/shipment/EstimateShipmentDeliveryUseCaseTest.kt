package org.bytebloom.logiroute.domain.usecase.shipment

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.dijkstra.DijkstraRouter
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class EstimateShipmentDeliveryUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var routeRepository: RouteRepository
    private lateinit var estimateShipmentDelivery: EstimateShipmentDeliveryUseCase

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        routeRepository = mockk()
        val findOptimalPath = FindOptimalPathUseCase(DijkstraRouter(createGraph()))
        estimateShipmentDelivery = EstimateShipmentDeliveryUseCase(packageRepository, routeRepository, findOptimalPath)
    }

    @Test
    fun `should sum route delays along optimal path when package exists`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(createPackage(destination = WAREHOUSE_C))
        coEvery { routeRepository.getAll() } returns listOf(ROUTE_A_TO_B, ROUTE_B_TO_C)

        // When
        val totalDelay = estimateShipmentDelivery(PACKAGE_ID)

        // Then
        assertThat(totalDelay).isEqualTo((DELAY_A_TO_B + DELAY_B_TO_C).toDouble())
    }

    @Test
    fun `should find package when id case differs`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(createPackage(destination = WAREHOUSE_B))
        coEvery { routeRepository.getAll() } returns listOf(ROUTE_A_TO_B)

        // When
        val totalDelay = estimateShipmentDelivery(PACKAGE_ID.lowercase())

        // Then
        assertThat(totalDelay).isEqualTo(DELAY_A_TO_B.toDouble())
    }

    @Test
    fun `should return null when package does not exist`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns emptyList()
        coEvery { routeRepository.getAll() } returns listOf(ROUTE_A_TO_B, ROUTE_B_TO_C)

        // When
        val totalDelay = estimateShipmentDelivery(PACKAGE_ID)

        // Then
        assertThat(totalDelay).isNull()
    }

    @Test
    fun `should return null when no path reaches destination`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(createPackage(destination = UNREACHABLE_WAREHOUSE))
        coEvery { routeRepository.getAll() } returns listOf(ROUTE_A_TO_B, ROUTE_B_TO_C)

        // When
        val totalDelay = estimateShipmentDelivery(PACKAGE_ID)

        // Then
        assertThat(totalDelay).isNull()
    }

    @Test
    fun `should return null when a route along the path is missing from repository`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(createPackage(destination = WAREHOUSE_C))
        coEvery { routeRepository.getAll() } returns listOf(ROUTE_A_TO_B)

        // When
        val totalDelay = estimateShipmentDelivery(PACKAGE_ID)

        // Then
        assertThat(totalDelay).isNull()
    }

    @Test
    fun `should propagate network exception when package repository fails`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } throws NetworkUnavailableException()
        coEvery { routeRepository.getAll() } returns emptyList()

        // When / Then
        assertFailsWith<NetworkUnavailableException> { estimateShipmentDelivery(PACKAGE_ID) }
    }

    private fun createGraph() = WarehouseGraph().apply {
        addRoute(WAREHOUSE_A, WAREHOUSE_B, ROUTE_A_TO_B.distanceKm)
        addRoute(WAREHOUSE_B, WAREHOUSE_C, ROUTE_B_TO_C.distanceKm)
        addWarehouse(UNREACHABLE_WAREHOUSE)
    }

    private fun createPackage(destination: Warehouse) = Package(
        id = PACKAGE_ID,
        weight = 10.0,
        priority = Priority.STANDARD,
        originWarehouse = WAREHOUSE_A,
        destinationWarehouse = destination
    )

    private companion object {
        const val PACKAGE_ID = "PKG-001"
        const val DELAY_A_TO_B = 15
        const val DELAY_B_TO_C = 25

        val WAREHOUSE_A = createWarehouse("WH-001")
        val WAREHOUSE_B = createWarehouse("WH-002")
        val WAREHOUSE_C = createWarehouse("WH-003")
        val UNREACHABLE_WAREHOUSE = createWarehouse("WH-004")

        val ROUTE_A_TO_B = createRoute("RT-001", WAREHOUSE_A, WAREHOUSE_B, DELAY_A_TO_B)
        val ROUTE_B_TO_C = createRoute("RT-002", WAREHOUSE_B, WAREHOUSE_C, DELAY_B_TO_C)

        fun createWarehouse(id: String) = Warehouse(
            id = id,
            name = "Hub $id",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )

        fun createRoute(id: String, origin: Warehouse, destination: Warehouse, delayMin: Int) = Route(
            id = id,
            distanceKm = 100.0,
            typicalDelayMin = delayMin,
            originWarehouse = origin,
            destinationWarehouse = destination
        )
    }
}