package usecase.queries

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


    @Test
    fun `should ignore vehicles with insufficient capacity`() = runTest {
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
            id = "PKG-000002",
            weight = 100.0,
            priority = Priority.STANDARD,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        val unsuitableVehicle = Vehicle(
            id = "TRK-003",
            maxCapacityKg = 80.0,
            costPerKm = 1.0,
            currentWarehouse = originWarehouse
        )

        val suitableVehicle = Vehicle(
            id = "TRK-004",
            maxCapacityKg = 150.0,
            costPerKm = 3.0,
            currentWarehouse = originWarehouse
        )

        coEvery {
            vehicleRepository.getAll()
        } returns listOf(unsuitableVehicle, suitableVehicle)

        val useCase = FindCheapestSuitableVehicleUseCase(vehicleRepository)

        // When
        val result = useCase(listOf(testPackage))

        // Then
        assertEquals(suitableVehicle, result)
    }
}
