package org.bytebloom.logiroute.domain.validator.id

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.id.RouteIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RouteIdValidatorTest {

    private lateinit var validator: RouteIdValidator

    @BeforeEach
    fun setUp() {
        validator = RouteIdValidator()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "RT-001",
            "RT-123456",
            "RT-550e8400-e29b-41d4-a716-446655440000"
        ]
    )
    fun `should return valid when route id has correct format`(routeId: String) {
        // Given
        val id = routeId

        // When
        val result = validator(id)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `should return blank violation when route id is blank`(routeId: String) {
        // Given
        val id = routeId

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "RT-12",
            "WH-001",
            "rt-001",
            "RT-00A",
            "RT001",
            "RT-001 "
        ]
    )
    fun `should return invalid format violation when route id is malformed`(routeId: String) {
        // Given
        val id = routeId

        // When
        val result = validator(id)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)

        val formatError = error as ValidatorError.InvalidIdFormat
        assertThat(formatError.field).isEqualTo(ValidatorField.ID)
        assertThat(formatError.entityType).isEqualTo(EntityType.ROUTE)
    }
}
