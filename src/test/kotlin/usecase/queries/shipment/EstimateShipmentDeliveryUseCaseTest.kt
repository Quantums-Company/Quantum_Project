package usecase.queries.shipment

import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.junit.jupiter.api.Test
import io.mockk.mockk
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Route
import io.mockk.coEvery
import org.bytebloom.logiroute.domain.usecase.shipment.EstimateShipmentDeliveryUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import kotlinx.coroutines.test.runTest
class EstimateShipmentDeliveryUseCaseTest {

    @Test
    fun `should return total delivery delay for existing package`() = runTest {
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


    @Test
    fun `should return null when package does not exist`() = runTest {
        // Given
        val packageRepository = mockk<PackageRepository>()
        val routeRepository = mockk<RouteRepository>()
        val findOptimalPath = mockk<FindOptimalPathUseCase>()

        coEvery { packageRepository.getAll() } returns emptyList()
        coEvery { routeRepository.getAll() } returns emptyList()

        val useCase = EstimateShipmentDeliveryUseCase(
            packageRepository,
            routeRepository,
            findOptimalPath
        )

        // When
        val result = useCase("PKG-999999")

        // Then
        assertEquals(null, result)
    }


    @Test
    fun `should return null when route is missing`() = runTest {
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

        val testPackage = Package(
            id = "PKG-000007",
            weight = 338.24,
            priority = Priority.STANDARD,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        coEvery { packageRepository.getAll() } returns listOf(testPackage)

        coEvery {
            findOptimalPath(originWarehouse, destinationWarehouse)
        } returns listOf(originWarehouse, destinationWarehouse)

        coEvery { routeRepository.getAll() } returns emptyList()

        val useCase = EstimateShipmentDeliveryUseCase(
            packageRepository,
            routeRepository,
            findOptimalPath
        )

        // When
        val result = useCase("PKG-000007")

        // Then
        assertEquals(null, result)
    }
}
