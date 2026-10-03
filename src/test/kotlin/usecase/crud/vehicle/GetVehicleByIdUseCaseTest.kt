package usecase.crud.vehicle

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.validator.id.VehicleIdValidator
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.usecase.crud.vehicle.GetVehicleByIdUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class GetVehicleByIdUseCaseTest {

    @Test
    fun `should return vehicle when id is valid and vehicle exists`() = runTest {
        // Given
        val vehicleRepository = mockk<VehicleRepository>()
        val validator = mockk<VehicleIdValidator>()

        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Warehouse",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val vehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = 200.0,
            costPerKm = 5.0,
            currentWarehouse = warehouse
        )

        every { validator("TRK-001") } returns ValidatorResult.Valid
        coEvery { vehicleRepository.getById("TRK-001") } returns vehicle

        val useCase = GetVehicleByIdUseCase(
            vehicleRepository,
            validator
        )

        // When
        val result = useCase("TRK-001")

        // Then
        assertEquals(vehicle, result)
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

        val useCase = GetVehicleByIdUseCase(
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
