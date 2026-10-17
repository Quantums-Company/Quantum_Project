package org.bytebloom.logiroute.domain.usecase.vehicles

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.usecase.crud.vehicles.DeleteVehicleUseCase
import org.bytebloom.logiroute.domain.validator.id.VehicleIdValidator
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeleteVehicleUseCaseTest {

    @Test
    fun `should delete vehicle successfully`() = runTest {
        // Given
        val vehicleRepository = mockk<VehicleRepository>()
        val validator = mockk<VehicleIdValidator>()

        every { validator("TRK-001") } returns ValidatorResult.Valid
        coEvery { vehicleRepository.delete("TRK-001") } returns true

        val useCase = DeleteVehicleUseCase(
            vehicleRepository,
            validator
        )

        // When
        val result = useCase("TRK-001")


        // Then
        assertTrue(result)
    }


    @Test
    fun `should throw exception when vehicle id is invalid`() = runTest {
        // Given
        val vehicleRepository = mockk<VehicleRepository>()
        val validator = mockk<VehicleIdValidator>()

        val violation = ValidatorError.InvalidIdFormat(
            ValidatorField.ID,
            EntityType.VEHICLE
        )

        every { validator("INVALID") } returns
                ValidatorResult.Invalid(listOf(violation))

        val useCase = DeleteVehicleUseCase(
            vehicleRepository,
            validator
        )

        // When & Then
        var exception: EntityValidationException? = null

        try {
            useCase("INVALID")
        } catch (e: EntityValidationException) {
            exception = e
        }

        assertNotNull(exception)
    }
}
