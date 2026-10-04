package validator.id

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.WarehouseIdValidator

class WarehouseIdValidatorTest {
    private val validator = WarehouseIdValidator()

    private fun violationsOf(result: ValidatorResult): List<ValidatorError> {
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        return (result as ValidatorResult.Invalid).violations }

    @Test
    fun `returns Valid for a sequential warehouse id`() {
        // Given
        val id = "WH-001"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `returns Valid for a UUID warehouse id`() {
        // Given
        val id = "WH-123e4567-e89b-12d3-a456-426614174000"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `returns Blank violation when id is blank`() {
        // Given
        val id = ""

        // When
        val violations = violationsOf(validator(id))

        // Then
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `returns InvalidIdFormat violation when id belongs to another entity`() {
        // Given
        val id = "PKG-001"

        // When
        val violations = violationsOf(validator(id))

        // Then
        assertThat(violations).hasSize(1)
        val error = violations.first() as ValidatorError.InvalidIdFormat
        assertThat(error.entityType).isEqualTo(EntityType.WAREHOUSE)
    }

    @Test
    fun `returns Invalid for malformed ids`() {
        // Given
        val tooShort = "WH-1"
        val wrongPrefix = "warehouse-001"

        // When
        val short = validator(tooShort)
        val wrong = validator(wrongPrefix)

        // Then
        assertThat(short).isInstanceOf(ValidatorResult.Invalid::class.java)
        assertThat(wrong).isInstanceOf(ValidatorResult.Invalid::class.java)
    }
}