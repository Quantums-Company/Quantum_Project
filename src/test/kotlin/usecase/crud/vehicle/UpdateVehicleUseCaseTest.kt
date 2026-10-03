package usecase.crud.vehicle

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.model.updateInput.VehicleUpdateInput
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.usecase.crud.vehicle.UpdateVehicleUseCase
import org.bytebloom.domain.validator.update.UpdateVehicleValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class UpdateVehicleUseCaseTest {

    @Test
    fun `should update vehicle successfully`() = runTest {
        // Given
        val vehicleRepository = mockk<VehicleRepository>()
        val validator = mockk<UpdateVehicleValidator>()

        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Warehouse",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val existingVehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = 200.0,
            costPerKm = 5.0,
            currentWarehouse = warehouse
        )

        val input = VehicleUpdateInput(
            id = "TRK-001",
            maxCapacityKg = 300.0,
            costPerKm = 4.0,
            currentWarehouse = warehouse
        )

        val updatedVehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = 300.0,
            costPerKm = 4.0,
            currentWarehouse = warehouse
        )

        every { validator(input) } returns ValidatorResult.Valid
        coEvery { vehicleRepository.getById("TRK-001") } returns existingVehicle
        coEvery { vehicleRepository.update(any()) } returns updatedVehicle

        val useCase = UpdateVehicleUseCase(
            vehicleRepository,
            validator
        )

        // When
        val result = useCase(input)

        // Then
        assertEquals(updatedVehicle, result)
    }


    @Test
    fun `should throw exception when vehicle does not exist`() = runTest {
        // Given
        val vehicleRepository = mockk<VehicleRepository>()
        val validator = mockk<UpdateVehicleValidator>()

        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Warehouse",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val input = VehicleUpdateInput(
            id = "TRK-999",
            maxCapacityKg = 300.0,
            costPerKm = 4.0,
            currentWarehouse = warehouse
        )

        every { validator(input) } returns ValidatorResult.Valid
        coEvery { vehicleRepository.getById("TRK-999") } returns null

        val useCase = UpdateVehicleUseCase(
            vehicleRepository,
            validator
        )

        // When & Then
        var exception: ResourceNotFoundException? = null

        try {
            useCase(input)
        } catch (e: ResourceNotFoundException) {
            exception = e
        }

        assertNotNull(exception)
    }


    @Test
    fun `should throw exception when vehicle update validation fails`() = runTest {
        // Given
        val vehicleRepository = mockk<VehicleRepository>()
        val validator = mockk<UpdateVehicleValidator>()

        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Warehouse",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        val input = VehicleUpdateInput(
            id = "TRK-001",
            maxCapacityKg = -100.0,
            costPerKm = 5.0,
            currentWarehouse = warehouse
        )

        val violation = ValidatorError.NotPositive(
            ValidatorField.MAX_CAPACITY_KG
        )

        every { validator(input) } returns
                ValidatorResult.Invalid(listOf(violation))

        val useCase = UpdateVehicleUseCase(
            vehicleRepository,
            validator
        )

        // When & Then
        var exception: EntityValidationException? = null

        try {
            useCase(input)
        } catch (e: EntityValidationException) {
            exception = e
        }

        assertNotNull(exception)
    }
}
