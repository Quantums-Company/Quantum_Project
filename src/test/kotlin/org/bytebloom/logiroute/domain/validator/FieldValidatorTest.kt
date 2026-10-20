
package org.bytebloom.logiroute.domain.validator

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.junit.jupiter.api.Test

class FieldValidatorTest {

    private val validator = FieldValidator()

    @Test
    fun `should return null when required text is not blank`() {
        // Given
        val text = "Main Hub"

        // When
        val result = validator.requiredText(text, ValidatorField.NAME)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return Blank error when required text is empty`() {
        // Given
        val text = ""

        // When
        val result = validator.requiredText(text, ValidatorField.NAME)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.NAME)
    }

    @Test
    fun `should return Blank error when required text contains only spaces`() {
        // Given
        val text = "   "

        // When
        val result = validator.requiredText(text, ValidatorField.REGIONAL_ZONE)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.REGIONAL_ZONE)
    }

    @Test
    fun `should return null when positive value is greater than zero`() {
        // Given
        val value = 5.0

        // When
        val result = validator.positive(value, ValidatorField.WEIGHT)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return NotPositive error when positive value is zero`() {
        // Given
        val value = 0.0

        // When
        val result = validator.positive(value, ValidatorField.WEIGHT)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NotPositive::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.WEIGHT)
    }

    @Test
    fun `should return NotPositive error when positive value is negative`() {
        // Given
        val value = -3.5

        // When
        val result = validator.positive(value, ValidatorField.WEIGHT)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NotPositive::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.WEIGHT)
    }

    @Test
    fun `should return NonFiniteNumber error when positive value is NaN`() {
        // Given
        val value = Double.NaN

        // When
        val result = validator.positive(value, ValidatorField.WEIGHT)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.WEIGHT)
    }

    @Test
    fun `should return NonFiniteNumber error when positive value is infinity`() {
        // Given
        val value = Double.POSITIVE_INFINITY

        // When
        val result = validator.positive(value, ValidatorField.WEIGHT)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.WEIGHT)
    }

    @Test
    fun `should return null when non-negative value is zero`() {
        // Given
        val value = 0

        // When
        val result = validator.nonNegative(value, ValidatorField.DISTANCE_KM)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return null when non-negative value is positive`() {
        // Given
        val value = 10

        // When
        val result = validator.nonNegative(value, ValidatorField.DISTANCE_KM)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return NegativeValue error when non-negative value is negative`() {
        // Given
        val value = -1

        // When
        val result = validator.nonNegative(value, ValidatorField.DISTANCE_KM)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NegativeValue::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @Test
    fun `should return null when latitude is at minimum boundary`() {
        // Given
        val value = -90.0

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return null when latitude is at middle boundary`() {
        // Given
        val value = 0.0

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return null when latitude is at maximum boundary`() {
        // Given
        val value = 90.0

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return OutOfRange error with correct field and bounds when latitude exceeds maximum`() {
        // Given
        val value = 90.1

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)

        val error = result as ValidatorError.OutOfRange
        assertThat(error.field).isEqualTo(ValidatorField.LATITUDE)
        assertThat(error.minimum).isEqualTo(-90.0)
        assertThat(error.maximum).isEqualTo(90.0)
    }

    @Test
    fun `should return OutOfRange error when latitude is below minimum`() {
        // Given
        val value = -90.1

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)

        val error = result as ValidatorError.OutOfRange
        assertThat(error.field).isEqualTo(ValidatorField.LATITUDE)
        assertThat(error.minimum).isEqualTo(-90.0)
        assertThat(error.maximum).isEqualTo(90.0)
    }

    @Test
    fun `should return NonFiniteNumber error when latitude is NaN`() {
        // Given
        val value = Double.NaN

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `should return NonFiniteNumber error when latitude is infinity`() {
        // Given
        val value = Double.POSITIVE_INFINITY

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `should return null when longitude is at minimum boundary`() {
        // Given
        val value = -180.0

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return null when longitude is at middle boundary`() {
        // Given
        val value = 0.0

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return null when longitude is at maximum boundary`() {
        // Given
        val value = 180.0

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should return OutOfRange error with correct field and bounds when longitude exceeds maximum`() {
        // Given
        val value = 180.1

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)

        val error = result as ValidatorError.OutOfRange
        assertThat(error.field).isEqualTo(ValidatorField.LONGITUDE)
        assertThat(error.minimum).isEqualTo(-180.0)
        assertThat(error.maximum).isEqualTo(180.0)
    }

    @Test
    fun `should return OutOfRange error when longitude is below minimum`() {
        // Given
        val value = -180.1

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)

        val error = result as ValidatorError.OutOfRange
        assertThat(error.field).isEqualTo(ValidatorField.LONGITUDE)
        assertThat(error.minimum).isEqualTo(-180.0)
        assertThat(error.maximum).isEqualTo(180.0)
    }

    @Test
    fun `should return NonFiniteNumber error when longitude is negative infinity`() {
        // Given
        val value = Double.NEGATIVE_INFINITY

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.LONGITUDE)
    }

    @Test
    fun `should return NonFiniteNumber error when longitude is NaN`() {
        // Given
        val value = Double.NaN

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.LONGITUDE)
    }
}
