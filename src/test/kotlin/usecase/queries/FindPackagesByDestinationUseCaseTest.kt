package usecase.queries

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.usecase.queries.FindPackagesByDestinationUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FindPackagesByDestinationUseCaseTest {

    @Test
    fun `should return packages by destination warehouse`() = runTest {
        // Given
        val packageRepository = mockk<PackageRepository>()

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
            id = "PKG-000001",
            weight = 100.0,
            priority = Priority.STANDARD,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        coEvery {
            packageRepository.getAll()
        } returns listOf(testPackage)

        val useCase = FindPackagesByDestinationUseCase(packageRepository)


        // When
        val result = useCase(destinationWarehouse)

        // Then
        assertEquals(listOf(testPackage), result)
    }


    @Test
    fun `should not return packages with different destination`() = runTest {

        // Given
        val packageRepository = mockk<PackageRepository>()

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

        val otherWarehouse = Warehouse(
            id = "WH-003",
            name = "Other",
            regionalZone = "East",
            longitude = 35.2,
            latitude = 32.2
        )

        val testPackage = Package(
            id = "PKG-000002",
            weight = 100.0,
            priority = Priority.STANDARD,
            originWarehouse = originWarehouse,
            destinationWarehouse = otherWarehouse
        )

        coEvery {
            packageRepository.getAll()
        } returns listOf(testPackage)

        val useCase = FindPackagesByDestinationUseCase(packageRepository)

        // When
        val result = useCase(destinationWarehouse)

        // Then
        assertEquals(emptyList<Package>(), result)
    }
}
