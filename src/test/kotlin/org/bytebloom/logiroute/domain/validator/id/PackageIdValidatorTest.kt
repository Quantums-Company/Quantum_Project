package validator.id

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.id.PackageIdValidator

import org.junit.jupiter.api.Test

class PackageIdValidatorTest {

    private val validator = PackageIdValidator()

    @Test
    fun `should accept a valid sequential package id`() {
        // Given
        val id = "PKG-001"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should accept a valid UUID package id`() {
        // Given
        val id = "PKG-550e8400-e29b-41d4-a716-446655440000"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should reject a blank package id`() {
        // Given
        val id = ""

        // When
        val result = validator(id)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorResult.Invalid::class.java)

        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)
        assertThat(invalid.violations.first())
            .isInstanceOf(ValidatorError.Blank::class.java)

        val error = invalid.violations.first() as ValidatorError.Blank
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `should reject an id with the wrong format`() {
        // Given
        val id = "WH-001"

        // When
        val result = validator(id)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorResult.Invalid::class.java)

        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)
        assertThat(invalid.violations.first())
            .isInstanceOf(ValidatorError.InvalidIdFormat::class.java)

        val error =
            invalid.violations.first() as ValidatorError.InvalidIdFormat

        assertThat(error.field).isEqualTo(ValidatorField.ID)
        assertThat(error.entityType).isEqualTo(EntityType.PACKAGE)
    }

    @Test
    fun `should reject a sequential id with fewer than three digits`() {
        // Given
        val id = "PKG-12"

        // When
        val result = validator(id)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorResult.Invalid::class.java)
    }
}
