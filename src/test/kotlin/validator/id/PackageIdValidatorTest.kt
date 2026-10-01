package validator.id

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.PackageIdValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class PackageIdValidatorTest {

    private val validator = PackageIdValidator()

    @Test
    fun `accepts a valid sequential package id`() {
        // Given
        val id = "PKG-001"

        // When
        val result = validator(id)

        // Then
        assertSame(ValidatorResult.Valid, result)
    }

    @Test
    fun `accepts a valid UUID package id`() {
        // Given
        val id = "PKG-550e8400-e29b-41d4-a716-446655440000"

        // When
        val result = validator(id)

        // Then
        assertSame(ValidatorResult.Valid, result)
    }

    @Test
    fun `rejects a blank package id`() {
        // Given
        val id = ""

        // When
        val result = validator(id)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.Blank>(invalid.violations.single())
        assertEquals(ValidatorField.ID, error.field)
    }

    @Test
    fun `rejects an id with the wrong format`() {
        // Given
        val id = "WH-001"

        // When
        val result = validator(id)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.InvalidIdFormat>(invalid.violations.single())
        assertEquals(ValidatorField.ID, error.field)
        assertEquals(EntityType.PACKAGE, error.entityType)
    }

    @Test
    fun `rejects a sequential id with fewer than three digits`() {
        // Given
        val id = "PKG-12"

        // When
        val result = validator(id)

        // Then
        assertIs<ValidatorResult.Invalid>(result)    }
}