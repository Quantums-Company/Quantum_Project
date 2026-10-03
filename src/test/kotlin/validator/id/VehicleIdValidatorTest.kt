package validator.id

import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.VehicleIdValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VehicleIdValidatorTest {

    @Test
    fun `should return valid when vehicle id is correct`() {
        // Given
        val validator = VehicleIdValidator()

        // When
        val result = validator("TRK-001")

        // Then
        assertEquals(ValidatorResult.Valid, result)
    }
}
