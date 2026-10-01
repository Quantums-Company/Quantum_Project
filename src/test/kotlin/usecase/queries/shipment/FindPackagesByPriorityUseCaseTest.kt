package usecase.queries.shipment

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.usecase.queries.FindPackagesByPriorityUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FindPackagesByPriorityUseCaseTest {

    @Test
    fun `should return packages by priority sorted by id`() = runTest {
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

        val firstPackage = Package(
            id = "PKG-000002",
            weight = 100.0,
            priority = Priority.URGENT,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        val secondPackage = Package(
            id = "PKG-000001",
            weight = 150.0,
            priority = Priority.URGENT,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        coEvery {
            packageRepository.getAll()
        } returns listOf(firstPackage, secondPackage)

        val useCase = FindPackagesByPriorityUseCase(packageRepository)

        // When
        val result = useCase(Priority.URGENT)

        // Then
        assertEquals(
            listOf(secondPackage, firstPackage),
            result
        )
    }
}
