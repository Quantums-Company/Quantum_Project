package org.bytebloom.logiroute.domain.usecase.vehicles

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.validator.id.VehicleIdValidator
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.usecase.crud.vehicles.GetVehicleByIdUseCase
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
