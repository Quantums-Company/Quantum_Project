package usecase.crud.vehicle

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.usecase.crud.vehicle.DeleteVehicleUseCase
import org.bytebloom.domain.validator.id.VehicleIdValidator
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
}
