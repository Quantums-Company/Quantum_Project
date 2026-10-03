package validator.update

import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.model.updateInput.VehicleUpdateInput
import org.bytebloom.domain.validator.update.UpdateVehicleValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateVehicleValidatorTest {

    @Test
    fun `should return valid when update data is correct`() {
        // Given
        val input = VehicleUpdateInput(
            id = "TRK-001",
            maxCapacityKg = 300.0,
            costPerKm = null,
            currentWarehouse = null
        )

        val validator = UpdateVehicleValidator()

        // When
        val result = validator(input)

        // Then
        assertEquals(ValidatorResult.Valid, result)
    }
}
