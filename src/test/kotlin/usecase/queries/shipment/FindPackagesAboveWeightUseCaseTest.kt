package usecase.queries.shipment

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.usecase.queries.FindPackagesAboveWeightUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FindPackagesAboveWeightUseCaseTest {

    @Test
    fun `should return packages above minimum weight`() = runTest {
        // Given
        val packageRepository = mockk<PackageRepository>()

        val firstWarehouse = Warehouse(
            id = "WH-001",
            name = "Warehouse 1",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val secondWarehouse = Warehouse(
            id = "WH-002",
            name = "Warehouse 2",
            regionalZone = "South",
            longitude = 35.1,
            latitude = 32.1
        )

        val lightPackage = Package(
            id = "PKG-000001",
            weight = 50.0,
            priority = Priority.STANDARD,
            originWarehouse = firstWarehouse,
            destinationWarehouse = secondWarehouse
        )

        val heavyPackage = Package(
            id = "PKG-000002",
            weight = 100.0,
            priority = Priority.STANDARD,
            originWarehouse = firstWarehouse,
            destinationWarehouse = secondWarehouse
        )

        coEvery {
            packageRepository.getAll()
        } returns listOf(lightPackage, heavyPackage)

        val useCase = FindPackagesAboveWeightUseCase(packageRepository)

        // When
        val result = useCase(100.0)

        // Then
        assertEquals(listOf(heavyPackage), result)
    }


    @Test
    fun `should include package when weight equals minimum weight`() = runTest {
        // Given
        val packageRepository = mockk<PackageRepository>()

        val firstWarehouse = Warehouse(
            id = "WH-001",
            name = "Warehouse 1",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val secondWarehouse = Warehouse(
            id = "WH-002",
            name = "Warehouse 2",
            regionalZone = "South",
            longitude = 35.1,
            latitude = 32.1
        )

        val packageAtMinimumWeight = Package(
            id = "PKG-000003",
            weight = 100.0,
            priority = Priority.STANDARD,
            originWarehouse = firstWarehouse,
            destinationWarehouse = secondWarehouse
        )

        coEvery {
            packageRepository.getAll()
        } returns listOf(packageAtMinimumWeight)

        val useCase = FindPackagesAboveWeightUseCase(packageRepository)

        // When
        val result = useCase(100.0)

        // Then
        assertEquals(listOf(packageAtMinimumWeight), result)
    }
}
