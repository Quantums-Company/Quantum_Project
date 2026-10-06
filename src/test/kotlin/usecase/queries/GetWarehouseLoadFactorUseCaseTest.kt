package usecase.queries

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.queries.GetWarehouseLoadFactorUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetWarehouseLoadFactorUseCaseTest {

    @Test
    fun `should calculate warehouse load factor`() {
        // Given
        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Warehouse",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val vehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = 200.0,
            costPerKm = 5.0,
            currentWarehouse = warehouse
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
            originWarehouse = warehouse,
            destinationWarehouse = destinationWarehouse
        )

        warehouse.addVehicle(vehicle)
        warehouse.addPackage(testPackage)

        val useCase = GetWarehouseLoadFactorUseCase()

        // When
        val result = useCase(warehouse)

        // Then
        assertEquals(0.5, result)
    }


    @Test
    fun `should return zero when warehouse has no vehicle capacity`() {
        // Given
        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Warehouse",
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
            originWarehouse = warehouse,
            destinationWarehouse = destinationWarehouse
        )

        warehouse.addPackage(testPackage)

        val useCase = GetWarehouseLoadFactorUseCase()

        // When
        val result = useCase(warehouse)

        // Then
        assertEquals(0.0, result)
    }
}
