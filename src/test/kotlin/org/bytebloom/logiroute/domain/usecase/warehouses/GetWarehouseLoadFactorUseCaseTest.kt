package org.bytebloom.logiroute.domain.usecase.warehouses

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetWarehouseLoadFactorUseCaseTest {

    private lateinit var useCase: GetWarehouseLoadFactorUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetWarehouseLoadFactorUseCase()
    }

    @Test
    fun `should return cargo weight divided by fleet capacity when warehouse has vehicles and cargo`() {
        // Given
        val cargoWeightKg = 100.0
        val fleetCapacityKg = 200.0
        val expectedLoadFactor = 0.5
        val warehouse = createWarehouse()
        warehouse.addVehicle(createVehicle(maxCapacityKg = fleetCapacityKg, currentWarehouse = warehouse))
        warehouse.addPackage(createPackage(weight = cargoWeightKg, originWarehouse = warehouse))

        // When
        val result = useCase(warehouse)

        // Then
        assertThat(result).isEqualTo(expectedLoadFactor)
    }

    @Test
    fun `should return zero when warehouse has no vehicle capacity`() {
        // Given
        val warehouse = createWarehouse()
        warehouse.addPackage(createPackage(originWarehouse = warehouse))

        // When
        val result = useCase(warehouse)

        // Then
        assertThat(result).isEqualTo(0.0)
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

    private fun createPackage(
        id: String = "PKG-000001",
        weight: Double = 100.0,
        priority: Priority = Priority.STANDARD,
        originWarehouse: Warehouse = createWarehouse(),
        destinationWarehouse: Warehouse = createWarehouse(id = "WH-002", name = "Destination")
    ) = Package(
        id = id,
        weight = weight,
        priority = priority,
        originWarehouse = originWarehouse,
        destinationWarehouse = destinationWarehouse
    )
}
