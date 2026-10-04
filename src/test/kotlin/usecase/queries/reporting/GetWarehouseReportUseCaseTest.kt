package usecase.queries.reporting

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.result.WarehouseReport
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.usecase.queries.reporting.GetWarehouseReportUseCase

class GetWarehouseReportUseCaseTest {
    private val repository = mockk<WarehouseRepository>()
    private val useCase = GetWarehouseReportUseCase(repository)

    private fun createWarehouse(id: String) = Warehouse(id = id, name = "Warehouse $id", regionalZone = "Central",
        longitude = 35.0, latitude = 31.0)

    @Test
    fun `should execute successfully`() = runTest {
        // Given
        val origin = createWarehouse("WH-001")
        val destination = createWarehouse("WH-002")
        origin.addPackage(Package("PKG-001", 10.5, Priority.URGENT, origin, destination))
        origin.addPackage(Package("PKG-002", 4.5, Priority.LOW, origin, destination))
        origin.addVehicle(Vehicle("TRK-001", 500.0, 2.0, origin))
        origin.addVehicle(Vehicle("TRK-002", 250.0, 3.0, origin))
        coEvery { repository.getAll() } returns listOf(origin, destination)

        // When
        val report = useCase("WH-001")

        // Then
        assertThat(report).isEqualTo(WarehouseReport(warehouseId = "WH-001", packageCount = 2, totalPackageWeight = 15.0,
            totalVehicleCapacity = 750.0))
    }

    @Test
    fun `returns zero totals when warehouse has no packages and no vehicles`() = runTest {
        // Given
        coEvery { repository.getAll() } returns listOf(createWarehouse("WH-001"))

        // When
        val report = useCase("WH-001")

        // Then
        assertThat(report).isEqualTo(WarehouseReport("WH-001", 0, 0.0, 0.0))
    }

    @Test
    fun `returns null when warehouse id does not exist`() = runTest {
        // Given
        coEvery { repository.getAll() } returns listOf(createWarehouse("WH-001"))

        // When
        val report = useCase("WH-999")

        // Then
        assertThat(report).isNull()
    }

    @Test
    fun `returns null when repository has no warehouses`() = runTest {
        // Given
        coEvery { repository.getAll() } returns emptyList()

        // When
        val report = useCase("WH-001")

        // Then
        assertThat(report).isNull()
    }

    @Test
    fun `counts only the data of the requested warehouse`() = runTest {
        // Given
        val first = createWarehouse("WH-001")
        val second = createWarehouse("WH-002")
        first.addPackage(Package("PKG-001", 10.0, Priority.STANDARD, first, second))
        second.addVehicle(Vehicle("TRK-001", 900.0, 2.0, second))
        coEvery { repository.getAll() } returns listOf(first, second)

        // When
        val report = useCase("WH-002")

        // Then
        assertThat(report).isEqualTo(WarehouseReport("WH-002", 0, 0.0, 900.0))
        coVerify(exactly = 1) { repository.getAll() }
    }
}