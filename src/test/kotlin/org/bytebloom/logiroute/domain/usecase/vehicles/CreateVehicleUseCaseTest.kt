package org.bytebloom.logiroute.domain.usecase.vehicles

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class CreateVehicleUseCaseTest {

    private val vehicleRepository: VehicleRepository = mockk()
    private val idGenerator: IdGenerator = mockk()
    private lateinit var useCase: CreateVehicleUseCase

    @BeforeEach
    fun setUp() {
        useCase = CreateVehicleUseCase(
            vehicleRepository = vehicleRepository,
            idGenerator = idGenerator
        )
    }

    @Test
    fun `should return created vehicle when data is valid`() = runTest {
        // Given
        val warehouse = createWarehouse()
        val createdVehicle = createVehicle(currentWarehouse = warehouse)
        every { idGenerator.next(EntityType.VEHICLE) } returns createdVehicle.id
        coEvery { vehicleRepository.create(any()) } returns createdVehicle

        // When
        val result = useCase(
            maxCapacityKg = createdVehicle.maxCapacityKg,
            costPerKm = createdVehicle.costPerKm,
            currentWarehouse = warehouse
        )

        // Then
        assertThat(result).isEqualTo(createdVehicle)
    }

    @Test
    fun `should save vehicle with generated id when data is valid`() = runTest {
        // Given
        val generatedId = "TRK-007"
        val maxCapacityKg = 500.0
        val costPerKm = 3.5
        val warehouse = createWarehouse()
        every { idGenerator.next(EntityType.VEHICLE) } returns generatedId
        coEvery { vehicleRepository.create(any()) } answers { firstArg() }

        // When
        val result = useCase(
            maxCapacityKg = maxCapacityKg,
            costPerKm = costPerKm,
            currentWarehouse = warehouse
        )

        // Then
        assertThat(result.id).isEqualTo(generatedId)
        assertThat(result.maxCapacityKg).isEqualTo(maxCapacityKg)
        assertThat(result.costPerKm).isEqualTo(costPerKm)
        assertThat(result.currentWarehouse).isEqualTo(warehouse)
    }

    @Test
    fun `should throw validation exception when capacity is zero`() = runTest {
        // Given
        val zeroCapacity = 0.0
        val warehouse = createWarehouse()
        every { idGenerator.next(EntityType.VEHICLE) } returns "TRK-001"

        // When
        val action = suspend {
            useCase(
                maxCapacityKg = zeroCapacity,
                costPerKm = createVehicle().costPerKm,
                currentWarehouse = warehouse
            )
        }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should throw validation exception when cost per km is negative`() = runTest {
        // Given
        val negativeCostPerKm = -1.2
        val warehouse = createWarehouse()
        every { idGenerator.next(EntityType.VEHICLE) } returns "TRK-001"

        // When
        val action = suspend {
            useCase(
                maxCapacityKg = createVehicle().maxCapacityKg,
                costPerKm = negativeCostPerKm,
                currentWarehouse = warehouse
            )
        }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should not call repository when capacity is zero`() = runTest {
        // Given
        val zeroCapacity = 0.0
        val warehouse = createWarehouse()
        every { idGenerator.next(EntityType.VEHICLE) } returns "TRK-001"

        // When
        runCatching {
            useCase(
                maxCapacityKg = zeroCapacity,
                costPerKm = createVehicle().costPerKm,
                currentWarehouse = warehouse
            )
        }

        // Then
        coVerify(exactly = 0) { vehicleRepository.create(any()) }
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
        maxCapacityKg: Double = 500.0,
        costPerKm: Double = 3.5,
        currentWarehouse: Warehouse = createWarehouse()
    ) = Vehicle(
        id = id,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm,
        currentWarehouse = currentWarehouse
    )
}
