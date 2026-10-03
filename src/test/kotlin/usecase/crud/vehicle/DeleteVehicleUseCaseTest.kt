package usecase.crud.vehicle

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.usecase.crud.vehicle.DeleteVehicleUseCase
import org.bytebloom.domain.validator.id.VehicleIdValidator
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
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
