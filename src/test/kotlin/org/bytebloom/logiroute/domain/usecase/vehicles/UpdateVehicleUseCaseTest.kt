package org.bytebloom.logiroute.domain.usecase.vehicles

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.update.VehicleUpdateInput
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.validator.update.UpdateVehicleValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class UpdateVehicleUseCaseTest {

    private val vehicleRepository: VehicleRepository = mockk()
    private val validator: UpdateVehicleValidator = mockk()
    private lateinit var useCase: UpdateVehicleUseCase

    @BeforeEach
    fun setUp() {
        useCase = UpdateVehicleUseCase(vehicleRepository, validator)
    }

    @Test
    fun `should return updated vehicle when input is valid and vehicle exists`() = runTest {
        // Given
        val existingVehicle = createVehicle(maxCapacityKg = 200.0, costPerKm = 5.0)
        val input = createUpdateInput(maxCapacityKg = 300.0, costPerKm = 4.0)
        val updatedVehicle = createVehicle(maxCapacityKg = 300.0, costPerKm = 4.0)
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { vehicleRepository.getById(existingVehicle.id) } returns existingVehicle
        coEvery { vehicleRepository.update(any()) } returns updatedVehicle

        // When
        val result = useCase(input)

        // Then
        assertThat(result).isEqualTo(updatedVehicle)
    }

    @Test
    fun `should throw resource not found exception when vehicle does not exist`() = runTest {
        // Given
        val unknownVehicleId = "TRK-999"
        val input = createUpdateInput(id = unknownVehicleId, maxCapacityKg = 300.0)
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { vehicleRepository.getById(unknownVehicleId) } returns null

        // When
        val action = suspend { useCase(input) }

        // Then
        assertFailsWith<ResourceNotFoundException> { action() }
    }

    @Test
    fun `should throw validation exception when vehicle update input is invalid`() = runTest {
        // Given
        val input = createUpdateInput(maxCapacityKg = -100.0)
        val violation = ValidatorError.NotPositive(ValidatorField.MAX_CAPACITY_KG)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(violation))

        // When
        val action = suspend { useCase(input) }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should not call repository when vehicle update input is invalid`() = runTest {
        // Given
        val input = createUpdateInput(maxCapacityKg = -100.0)
        val violation = ValidatorError.NotPositive(ValidatorField.MAX_CAPACITY_KG)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(violation))

        // When
        runCatching { useCase(input) }

        // Then
        coVerify(exactly = 0) { vehicleRepository.getById(any()) }
        coVerify(exactly = 0) { vehicleRepository.update(any()) }
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

    private fun createUpdateInput(
        id: String = "TRK-001",
        maxCapacityKg: Double? = null,
        costPerKm: Double? = null,
        currentWarehouse: Warehouse? = null
    ) = VehicleUpdateInput(
        id = id,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm,
        currentWarehouse = currentWarehouse
    )
}
