package validator.create

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.create.CreateVehicleValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CreateVehicleValidatorTest {

    @Test
    fun `should return valid when vehicle data is correct`() {
        // Given
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

        val validator = CreateVehicleValidator()

        // When
        val result = validator(vehicle)

        // Then
        assertEquals(ValidatorResult.Valid, result)
    }


    @Test
    fun `should return invalid when max capacity is not positive`() {
        // Given
        val vehicle = mockk<Vehicle>()

        val warehouse = mockk<Warehouse>()

        every { vehicle.id } returns "TRK-001"
        every { vehicle.maxCapacityKg } returns -100.0
        every { vehicle.costPerKm } returns 5.0
        every { vehicle.currentWarehouse } returns warehouse
        every { warehouse.id } returns "WH-001"

        val validator = CreateVehicleValidator()

        // When
        val result = validator(vehicle)

        // Then
        assertTrue(result is ValidatorResult.Invalid)
    }

}
