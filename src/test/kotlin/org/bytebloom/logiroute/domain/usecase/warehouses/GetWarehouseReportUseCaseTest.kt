package org.bytebloom.logiroute.domain.usecase.warehouses

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.result.WarehouseReport
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetWarehouseReportUseCaseTest {

    private val repository: WarehouseRepository = mockk()
    private lateinit var useCase: GetWarehouseReportUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetWarehouseReportUseCase(repository)
    }

    @Test
    fun `should return report with package and vehicle totals when warehouse has packages and vehicles`() = runTest {
        // Given
        val origin = createWarehouse(id = "WH-001")
        val destination = createWarehouse(id = "WH-002")
        origin.addPackage(createPackage(id = "PKG-001", weight = 10.5, originWarehouse = origin, destinationWarehouse = destination))
        origin.addPackage(createPackage(id = "PKG-002", weight = 4.5, originWarehouse = origin, destinationWarehouse = destination))
        origin.addVehicle(createVehicle(id = "TRK-001", maxCapacityKg = 500.0, currentWarehouse = origin))
        origin.addVehicle(createVehicle(id = "TRK-002", maxCapacityKg = 250.0, currentWarehouse = origin))
        coEvery { repository.getAll() } returns listOf(origin, destination)

        // When
        val report = useCase(origin.id)

        // Then
        val expectedReport = WarehouseReport(
            warehouseId = origin.id,
            packageCount = 2,
            totalPackageWeight = 15.0,
            totalVehicleCapacity = 750.0
        )
        assertThat(report).isEqualTo(expectedReport)
    }

    @Test
    fun `should return zero totals when warehouse has no packages and no vehicles`() = runTest {
        // Given
        val emptyWarehouse = createWarehouse(id = "WH-001")
        coEvery { repository.getAll() } returns listOf(emptyWarehouse)

        // When
        val report = useCase(emptyWarehouse.id)

        // Then
        val expectedReport = WarehouseReport(
            warehouseId = emptyWarehouse.id,
            packageCount = 0,
            totalPackageWeight = 0.0,
            totalVehicleCapacity = 0.0
        )
        assertThat(report).isEqualTo(expectedReport)
    }

    @Test
    fun `should return null when warehouse id does not exist`() = runTest {
        // Given
        val unknownWarehouseId = "WH-999"
        coEvery { repository.getAll() } returns listOf(createWarehouse(id = "WH-001"))

        // When
        val report = useCase(unknownWarehouseId)

        // Then
        assertThat(report).isNull()
    }

    @Test
    fun `should return null when repository has no warehouses`() = runTest {
        // Given
        coEvery { repository.getAll() } returns emptyList()

        // When
        val report = useCase("WH-001")

        // Then
        assertThat(report).isNull()
    }

    @Test
    fun `should return report containing only the data of the requested warehouse`() = runTest {
        // Given
        val first = createWarehouse(id = "WH-001")
        val second = createWarehouse(id = "WH-002")
        val secondWarehouseCapacityKg = 900.0
        first.addPackage(createPackage(originWarehouse = first, destinationWarehouse = second))
        second.addVehicle(createVehicle(maxCapacityKg = secondWarehouseCapacityKg, currentWarehouse = second))
        coEvery { repository.getAll() } returns listOf(first, second)

        // When
        val report = useCase(second.id)

        // Then
        val expectedReport = WarehouseReport(
            warehouseId = second.id,
            packageCount = 0,
            totalPackageWeight = 0.0,
            totalVehicleCapacity = secondWarehouseCapacityKg
        )
        assertThat(report).isEqualTo(expectedReport)
    }

    @Test
    fun `should fetch all warehouses from repository exactly once`() = runTest {
        // Given
        val first = createWarehouse(id = "WH-001")
        val second = createWarehouse(id = "WH-002")
        coEvery { repository.getAll() } returns listOf(first, second)

        // When
        useCase(second.id)

        // Then
        coVerify(exactly = 1) { repository.getAll() }
    }

    private fun createWarehouse(
        id: String = "WH-001",
        name: String = "Warehouse $id",
        regionalZone: String = "Central",
        longitude: Double = 35.0,
        latitude: Double = 31.0
    ) = Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )

    private fun createPackage(
        id: String = "PKG-001",
        weight: Double = 10.0,
        priority: Priority = Priority.STANDARD,
        originWarehouse: Warehouse = createWarehouse(id = "WH-001"),
        destinationWarehouse: Warehouse = createWarehouse(id = "WH-002")
    ) = Package(
        id = id,
        weight = weight,
        priority = priority,
        originWarehouse = originWarehouse,
        destinationWarehouse = destinationWarehouse
    )

    private fun createVehicle(
        id: String = "TRK-001",
        maxCapacityKg: Double = 500.0,
        costPerKm: Double = 2.0,
        currentWarehouse: Warehouse = createWarehouse()
    ) = Vehicle(
        id = id,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm,
        currentWarehouse = currentWarehouse
    )
}
