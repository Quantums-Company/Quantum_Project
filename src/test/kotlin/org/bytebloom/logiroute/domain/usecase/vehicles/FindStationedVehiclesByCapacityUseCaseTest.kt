package org.bytebloom.logiroute.domain.usecase.vehicles

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FindStationedVehiclesByCapacityUseCaseTest {

    private lateinit var useCase: FindStationedVehiclesByCapacityUseCase

    @BeforeEach
    fun setUp() {
        useCase = FindStationedVehiclesByCapacityUseCase()
    }

    @Test
    fun `should return only vehicles that meet required capacity`() {
        // Given
        val requiredCapacityKg = 100.0
        val warehouse = createWarehouse()
        val suitableVehicle = createVehicle(id = "TRK-001", maxCapacityKg = 200.0, currentWarehouse = warehouse)
        val unsuitableVehicle = createVehicle(id = "TRK-002", maxCapacityKg = 80.0, currentWarehouse = warehouse)
        warehouse.addVehicle(suitableVehicle)
        warehouse.addVehicle(unsuitableVehicle)

        // When
        val result = useCase(warehouse, requiredCapacityKg)

        // Then
        assertThat(result).containsExactly(suitableVehicle)
    }

    @Test
    fun `should include vehicle when capacity exactly matches requirement`() {
        // Given
        val requiredCapacityKg = 100.0
        val warehouse = createWarehouse()
        val exactCapacityVehicle = createVehicle(maxCapacityKg = requiredCapacityKg, currentWarehouse = warehouse)
        warehouse.addVehicle(exactCapacityVehicle)

        // When
        val result = useCase(warehouse, requiredCapacityKg)

        // Then
        assertThat(result).containsExactly(exactCapacityVehicle)
    }

    @Test
    fun `should return empty list when warehouse has no stationed vehicles`() {
        // Given
        val requiredCapacityKg = 100.0
        val warehouse = createWarehouse()

        // When
        val result = useCase(warehouse, requiredCapacityKg)

        // Then
        assertThat(result).isEmpty()
    }

    private fun createWarehouse(
        id: String = "WH-001",
        name: String = "Main Warehouse",
        regionalZone: String = "North",
        longitude: Double = 35.0,
        latitude: Double = 32.0
    ) = Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )

    private fun createVehicle(
        id: String = "TRK-001",
        maxCapacityKg: Double = 200.0,
        costPerKm: Double = 5.0,
        currentWarehouse: Warehouse = createWarehouse()
    ) = Vehicle(
        id = id,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm,
        currentWarehouse = currentWarehouse
    )
}
