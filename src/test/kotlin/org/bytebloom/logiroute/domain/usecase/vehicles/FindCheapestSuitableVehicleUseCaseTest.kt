package org.bytebloom.logiroute.domain.usecase.vehicles

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FindCheapestSuitableVehicleUseCaseTest {

    private val vehicleRepository: VehicleRepository = mockk()
    private lateinit var useCase: FindCheapestSuitableVehicleUseCase

    @BeforeEach
    fun setUp() {
        useCase = FindCheapestSuitableVehicleUseCase(vehicleRepository)
    }

    @Test
    fun `should return cheapest vehicle when several vehicles can carry the packages`() = runTest {
        // Given
        val packageToCarry = createPackage(weight = 100.0)
        val expensiveVehicle = createVehicle(id = "TRK-001", maxCapacityKg = 200.0, costPerKm = 5.0)
        val cheapestVehicle = createVehicle(id = "TRK-002", maxCapacityKg = 200.0, costPerKm = 2.0)
        coEvery { vehicleRepository.getAll() } returns listOf(expensiveVehicle, cheapestVehicle)

        // When
        val result = useCase(listOf(packageToCarry))

        // Then
        assertThat(result).isEqualTo(cheapestVehicle)
    }

    @Test
    fun `should return suitable vehicle when cheaper vehicle has insufficient capacity`() = runTest {
        // Given
        val packageToCarry = createPackage(weight = 100.0)
        val tooSmallVehicle = createVehicle(id = "TRK-003", maxCapacityKg = 80.0, costPerKm = 1.0)
        val suitableVehicle = createVehicle(id = "TRK-004", maxCapacityKg = 150.0, costPerKm = 3.0)
        coEvery { vehicleRepository.getAll() } returns listOf(tooSmallVehicle, suitableVehicle)

        // When
        val result = useCase(listOf(packageToCarry))

        // Then
        assertThat(result).isEqualTo(suitableVehicle)
    }

    @Test
    fun `should return null when no vehicle can carry the packages`() = runTest {
        // Given
        val packageToCarry = createPackage(weight = 100.0)
        val tooSmallVehicle = createVehicle(maxCapacityKg = 80.0)
        coEvery { vehicleRepository.getAll() } returns listOf(tooSmallVehicle)

        // When
        val result = useCase(listOf(packageToCarry))

        // Then
        assertThat(result).isNull()
    }

    private fun createWarehouse(
        id: String = "WH-001",
        name: String = "Origin",
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
        originWarehouse: Warehouse = createWarehouse(id = "WH-001", name = "Origin"),
        destinationWarehouse: Warehouse = createWarehouse(id = "WH-002", name = "Destination")
    ) = Package(
        id = id,
        weight = weight,
        priority = priority,
        originWarehouse = originWarehouse,
        destinationWarehouse = destinationWarehouse
    )
}
