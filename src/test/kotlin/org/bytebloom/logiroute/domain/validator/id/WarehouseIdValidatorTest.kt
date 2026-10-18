package org.bytebloom.logiroute.domain.validator.id

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult

class WarehouseIdValidatorTest {

    private val validator = WarehouseIdValidator()

    @Test
    fun `should return valid when warehouse id has correct sequential format`() {
        // Given
        val id = "WH-001"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return valid when warehouse id has correct UUID format`() {
        // Given
        val id = "WH-123e4567-e89b-12d3-a456-426614174000"

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return blank violation when warehouse id is empty`() {
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
    fun `should return blank violation when warehouse id contains only whitespace`() {
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
    fun `should return invalid format violation when id belongs to another entity`() {
        // Given
        val id = "PKG-001"

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)

        val formatError = error as ValidatorError.InvalidIdFormat
        assertThat(formatError.field).isEqualTo(ValidatorField.ID)
        assertThat(formatError.entityType).isEqualTo(EntityType.WAREHOUSE)
    }

    @Test
    fun `should return invalid format violation when warehouse id is too short`() {
        // Given
        val id = "WH-1"

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `should return invalid format violation when warehouse id has wrong prefix`() {
        // Given
        val id = "warehouse-001"

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }
}