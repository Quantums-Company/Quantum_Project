package usecase.queries.shipment

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.usecase.queries.FindCheapestSuitableVehicleUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FindCheapestSuitableVehicleUseCaseTest {

    @Test
    fun `should return cheapest suitable vehicle`() = runTest  {
        // Given
        val vehicleRepository = mockk<VehicleRepository>()

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

        val expensiveVehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = 200.0,
            costPerKm = 5.0,
            currentWarehouse = originWarehouse
        )

        val cheapestVehicle = Vehicle(
            id = "TRK-002",
            maxCapacityKg = 200.0,
            costPerKm = 2.0,
            currentWarehouse = originWarehouse
        )

        coEvery {
            vehicleRepository.getAll()
        } returns listOf(expensiveVehicle, cheapestVehicle)

        val useCase = FindCheapestSuitableVehicleUseCase(vehicleRepository)

        // When
        val result = useCase(listOf(testPackage))

        // Then
        assertEquals(cheapestVehicle, result)
    }
}
