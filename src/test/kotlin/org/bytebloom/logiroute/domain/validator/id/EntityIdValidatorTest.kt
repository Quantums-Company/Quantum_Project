package org.bytebloom.logiroute.domain.validator.id

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.id.EntityIdValidator

class EntityIdValidatorTest {
    private val validator = EntityIdValidator()

    private fun violationsOf(result: ValidatorResult): List<ValidatorError> {
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        return (result as ValidatorResult.Invalid).violations
    }

    @Test
    fun `should accept a three-digit sequential id when entity type is warehouse`() {
        // Given
        val threeDigitsId = "WH-001"

        // When
        val result = validator.validate(threeDigitsId, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should accept a five-digit sequential id when entity type is warehouse`() {
        // Given
        val fiveDigitsId = "WH-12345"

        // When
        val result = validator.validate(fiveDigitsId, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should accept a UUID id when entity type is warehouse`() {
        // Given
        val id = "WH-123e4567-e89b-12d3-a456-426614174000"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return a blank error when id is blank`() {
        // Given
        val id = "   "

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.ID)
    }


    @Test
    fun `should return an invalid format error with the expected entity type when prefix belongs to another entity`() {
        // Given
        val id = "PKG-001"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)

        val error = result as ValidatorError.InvalidIdFormat
        assertThat(error.entityType).isEqualTo(EntityType.WAREHOUSE)
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }



    @Test
    fun `should return an invalid format error when id contains trailing characters`() {
        // Given
        val id = "WH-001x"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorError.InvalidIdFormat::class.java)

        val error = result as ValidatorError.InvalidIdFormat
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `should return invalid format error when id is only prefix`() {
        // Given
        val id = "WH-"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }

    @Test
    fun `should return invalid format error when id has two digits`() {
        // Given
        val id = "WH-01"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }

    @Test
    fun `should return invalid format error when id contains letters instead of digits`() {
        // Given
        val id = "WH-abc"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }

    @Test
    fun `should return invalid format error when id has no prefix`() {
        // Given
        val id = "001"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }



    @Test
    fun `should return invalid format error when UUID id is incomplete`() {
        // Given
        val id = "WH-123e4567-e89b-12d3-a456"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result)
            .isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }


    @Test
    fun `should accept a valid package id when entity type is package`() {
        // Given
        val packageId = "PKG-001"

        // When
        val packageResult = validator.validate(packageId, EntityType.PACKAGE)

        // Then
        assertThat(packageResult).isNull()
    }

    @Test
    fun `should accept a valid route id when entity type is route`() {
        // Given
        val routeId = "RT-001"

        // When
        val routeResult = validator.validate(routeId, EntityType.ROUTE)

        // Then
        assertThat(routeResult).isNull()
    }

    @Test
    fun `should accept a valid vehicle id when entity type is vehicle`() {
        // Given
        val vehicleId = "TRK-001"

        // When
        val vehicleResult = validator.validate(vehicleId, EntityType.VEHICLE)

        // Then
        assertThat(vehicleResult).isNull()
    }

    @Test
    fun `should return a valid result when id is correct`() {
        // Given
        val id = "WH-001"

        // When
        val result = validator(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }



    @Test
    fun `should return one invalid format violation when id is incorrect`() {
        // Given
        val id = "bad-id"

        // When
        val result = validator(id, EntityType.WAREHOUSE)

        // Then
        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }
}