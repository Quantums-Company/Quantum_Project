
package org.bytebloom.logiroute.domain.validator.update

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.input.update.WarehouseUpdateInput
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.junit.jupiter.api.Test

class UpdateWarehouseValidatorTest {

    private val validator = UpdateWarehouseValidator()

    private fun violationsOf(result: ValidatorResult): List<ValidatorError> {
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        return (result as ValidatorResult.Invalid).violations
    }

    @Test
    fun `should return valid when one update field is provided`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name")

        // When
        val result = validator(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return valid when all update fields are valid`() {
        // Given
        val input = WarehouseUpdateInput(
            id = "WH-001",
            name = "New Name",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 31.0
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return NoFieldsProvided violation when no update fields are provided`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001")

        // When
        val result = validator(input)

        // Then
        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)

        val error = violations.single()
        assertThat(error).isInstanceOf(ValidatorError.NoFieldsProvided::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ENTITY)
    }

    @Test
    fun `should return InvalidIdFormat violation when id is invalid`() {
        // Given
        val input = WarehouseUpdateInput(id = "bad-id", name = "New Name")

        // When
        val result = validator(input)

        // Then
        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)

        val error = violations.single()
        assertThat(error).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `should return Blank violation when provided name is blank`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001", name = " ")

        // When
        val result = validator(input)

        // Then
        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)

        val error = violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.NAME)
    }

    @Test
    fun `should return Blank violation when provided regional zone is blank`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001", regionalZone = "")

        // When
        val result = validator(input)

        // Then
        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)

        val error = violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.REGIONAL_ZONE)
    }

    @Test
    fun `should return OutOfRange violation when latitude is out of range`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001", latitude = 91.0)

        // When
        val result = validator(input)

        // Then
        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)

        val error = violations.single()
        assertThat(error).isInstanceOf(ValidatorError.OutOfRange::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `should return OutOfRange violation when longitude is out of range`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001", longitude = 181.0)

        // When
        val result = validator(input)

        // Then
        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)

        val error = violations.single()
        assertThat(error).isInstanceOf(ValidatorError.OutOfRange::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.LONGITUDE)
    }

    @Test
    fun `should return all violations when multiple inputs are invalid`() {
        // Given
        val input = WarehouseUpdateInput(
            id = "bad",
            name = "",
            regionalZone = " ",
            longitude = 500.0,
            latitude = -100.0
        )

        // When
        val result = validator(input)

        // Then
        val violations = violationsOf(result)

        assertThat(violations).hasSize(5)
        assertThat(violations.map { it.field }.toSet()).isEqualTo(
            setOf(
                ValidatorField.ID,
                ValidatorField.NAME,
                ValidatorField.REGIONAL_ZONE,
                ValidatorField.LATITUDE,
                ValidatorField.LONGITUDE
            )
        )
    }
}
