package usecase.queries.shipment

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.queries.FindStationedVehiclesByCapacityUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FindStationedVehiclesByCapacityUseCaseTest {

    @Test
    fun `should return vehicles that meet required capacity`() {
        // Given
        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Warehouse",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val suitableVehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = 200.0,
            costPerKm = 5.0,
            currentWarehouse = warehouse
        )

        val unsuitableVehicle = Vehicle(
            id = "TRK-002",
            maxCapacityKg = 80.0,
            costPerKm = 3.0,
            currentWarehouse = warehouse
        )

        warehouse.addVehicle(suitableVehicle)
        warehouse.addVehicle(unsuitableVehicle)

        val useCase = FindStationedVehiclesByCapacityUseCase()


        // When
        val result = useCase(warehouse, 100.0)

        // Then
        assertEquals(listOf(suitableVehicle), result)
    }
}
