package org.bytebloom.logiroute.domain.usecase.vehicles

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.validator.id.VehicleIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DeleteVehicleUseCaseTest {

    private val vehicleRepository: VehicleRepository = mockk()
    private val validator: VehicleIdValidator = mockk()
    private lateinit var useCase: DeleteVehicleUseCase

    @BeforeEach
    fun setUp() {
        useCase = DeleteVehicleUseCase(vehicleRepository, validator)
    }

    @Test
    fun `should return true when vehicle is deleted`() = runTest {
        // Given
        val vehicleId = "TRK-001"
        every { validator(vehicleId) } returns ValidatorResult.Valid
        coEvery { vehicleRepository.delete(vehicleId) } returns true

        // When
        val result = useCase(vehicleId)

        // Then
        assertThat(result).isTrue()
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
        coVerify(exactly = 0) { vehicleRepository.delete(any()) }
    }

    private fun createInvalidIdError() = ValidatorError.InvalidIdFormat(
        ValidatorField.ID,
        EntityType.VEHICLE
    )
}
