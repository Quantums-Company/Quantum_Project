package validator.id

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.VehicleIdValidator
import org.junit.jupiter.api.Test

class VehicleIdValidatorTest {

    private val validator = VehicleIdValidator()

    @Test
    fun `should return valid when vehicle id has correct sequential format`() {
        // Given
        val id = "TRK-001"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return valid when vehicle id has correct UUID format`() {
        // Given
        val id = "TRK-550e8400-e29b-41d4-a716-446655440000"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return blank violation when vehicle id is empty`() {
        // Given
        val id = ""

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `should return blank violation when vehicle id contains only whitespace`() {
        // Given
        val id = "   "

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `should return invalid format violation when vehicle id has wrong prefix`() {
        // Given
        val id = "WH-001"

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)

        val formatError = error as ValidatorError.InvalidIdFormat
        assertThat(formatError.field).isEqualTo(ValidatorField.ID)
        assertThat(formatError.entityType).isEqualTo(EntityType.VEHICLE)
    }

    @Test
    fun `should return invalid format violation when vehicle id is malformed`() {
        // Given
        val id = "TRK-00A"

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)

        val formatError = error as ValidatorError.InvalidIdFormat
        assertThat(formatError.field).isEqualTo(ValidatorField.ID)
        assertThat(formatError.entityType).isEqualTo(EntityType.VEHICLE)
    }
}
