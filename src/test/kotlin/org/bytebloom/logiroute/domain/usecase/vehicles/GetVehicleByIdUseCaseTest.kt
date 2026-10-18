package org.bytebloom.logiroute.domain.usecase.vehicles

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.validator.id.VehicleIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class GetVehicleByIdUseCaseTest {

    private val vehicleRepository: VehicleRepository = mockk()
    private val validator: VehicleIdValidator = mockk()
    private lateinit var useCase: GetVehicleByIdUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetVehicleByIdUseCase(vehicleRepository, validator)
    }

    @Test
    fun `should return vehicle when id is valid and vehicle exists`() = runTest {
        // Given
        val vehicle = createVehicle(id = "TRK-001")
        every { validator(vehicle.id) } returns ValidatorResult.Valid
        coEvery { vehicleRepository.getById(vehicle.id) } returns vehicle

        // When
        val result = useCase(vehicle.id)

        // Then
        assertThat(result).isEqualTo(vehicle)
    }

    @Test
    fun `should return null when id is valid but vehicle does not exist`() = runTest {
        // Given
        val unknownVehicleId = "TRK-999"
        every { validator(unknownVehicleId) } returns ValidatorResult.Valid
        coEvery { vehicleRepository.getById(unknownVehicleId) } returns null

        // When
        val result = useCase(unknownVehicleId)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should throw validation exception when vehicle id is invalid`() = runTest {
        // Given
        val invalidId = "INVALID"
        every { validator(invalidId) } returns ValidatorResult.Invalid(listOf(createInvalidIdError()))

        // When
        val action = suspend { useCase(invalidId) }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should not call repository when vehicle id is invalid`() = runTest {
        // Given
        val invalidId = "INVALID"
        every { validator(invalidId) } returns ValidatorResult.Invalid(listOf(createInvalidIdError()))

        // When
        runCatching { useCase(invalidId) }

        // Then
        coVerify(exactly = 0) { vehicleRepository.getById(any()) }
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

    private fun createInvalidIdError() = ValidatorError.InvalidIdFormat(
        ValidatorField.ID,
        EntityType.VEHICLE
    )
}
