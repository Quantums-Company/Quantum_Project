package usecase.queries.shipment

import org.bytebloom.domain.repository.PackageRepository
import org.junit.jupiter.api.Test
import io.mockk.mockk
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.usecase.queries.routing.FindOptimalPathUseCase
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Route
import io.mockk.coEvery
import org.bytebloom.domain.usecase.queries.shipment.EstimateShipmentDeliveryUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import kotlinx.coroutines.test.runTest
class EstimateShipmentDeliveryUseCaseTest {

    @Test
    fun `should execute successfully`() = runTest {
        // Given
        val packageRepository = mockk<PackageRepository>()
        val routeRepository = mockk<RouteRepository>()
        val findOptimalPath = mockk<FindOptimalPathUseCase>()

        val originWarehouse = Warehouse(
            id = "WH-001",
            name = "Origin",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val destinationWarehouse = Warehouse(
            id = "WH-002",
            name = "Destination",
            regionalZone = "South",
            longitude = 35.1,
            latitude = 32.1
        )

        val testPackage = Package (
            id = "PKG-000007",
            weight = 338.24,
            priority = Priority.STANDARD,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        val route = Route(
            id = "RT-00001",
            distanceKm = 100.0,
            typicalDelayMin = 30,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        coEvery { packageRepository.getAll() } returns listOf(testPackage)

        coEvery {
            findOptimalPath(originWarehouse, destinationWarehouse)
        } returns listOf(originWarehouse, destinationWarehouse)

        coEvery { routeRepository.getAll() } returns listOf(route)




        // When

        val useCase = EstimateShipmentDeliveryUseCase(
            packageRepository,
            routeRepository,
            findOptimalPath
        )

        val result = useCase("PKG-000007")

        // Then

        assertEquals(30.0, result)
    }
}
