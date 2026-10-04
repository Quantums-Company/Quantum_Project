package validator

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.validator.FieldValidator

class FieldValidatorTest {
    private val validator = FieldValidator()

    @Test
    fun `requiredText returns null when text is not blank`() {
        // Given
        val text = "Main Hub"

        // When
        val result = validator.requiredText(text, ValidatorField.NAME)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `requiredText returns Blank error when text is empty`() {
        // Given
        val text = ""

        // When
        val result = validator.requiredText(text, ValidatorField.NAME)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.NAME)
    }

    @Test
    fun `requiredText returns Blank error when text contains only spaces`() {
        // Given
        val text = "   "

        // When
        val result = validator.requiredText(text, ValidatorField.REGIONAL_ZONE)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.REGIONAL_ZONE)
    }

    @Test
    fun `positive returns null when value is greater than zero`() {
        // Given
        val value = 5.0

        // When
        val result = validator.positive(value, ValidatorField.WEIGHT)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `positive returns NotPositive error when value is zero`() {
        // Given
        val value = 0.0

        // When
        val result = validator.positive(value, ValidatorField.WEIGHT)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NotPositive::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.WEIGHT)
    }

    @Test
    fun `positive returns NotPositive error when value is negative`() {
        // Given
        val value = -3.5

        // When
        val result = validator.positive(value, ValidatorField.WEIGHT)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NotPositive::class.java)
    }

    @Test
    fun `positive returns NonFiniteNumber error for NaN and infinity`() {
        val notANumber = Double.NaN
        val infinity = Double.POSITIVE_INFINITY

        // When
        val nanResult = validator.positive(notANumber, ValidatorField.WEIGHT)
        val infinityResult = validator.positive(infinity, ValidatorField.WEIGHT)

        // Then
        assertThat(nanResult).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(infinityResult).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
    }

    @Test
    fun `nonNegative returns null for zero and positive values`() {
        // Given
        val zero = 0
        val positive = 10

        // When
        val zeroResult = validator.nonNegative(zero, ValidatorField.DISTANCE_KM)
        val positiveResult = validator.nonNegative(positive, ValidatorField.DISTANCE_KM)

        // Then
        assertThat(zeroResult).isNull()
        assertThat(positiveResult).isNull()
    }

    @Test
    fun `nonNegative returns NegativeValue error when value is below zero`() {
        // Given
        val value = -1

        // When
        val result = validator.nonNegative(value, ValidatorField.DISTANCE_KM)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NegativeValue::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @Test
    fun `latitude returns null for values inside the range including both limits`() {
        // Given
        val mid = 0.0
        val min = -90.0
        val max = 90.0

        // When
        val middle = validator.latitude(mid)
        val minimum = validator.latitude(min)
        val maximum = validator.latitude(max)

        // Then
        assertThat(middle).isNull()
        assertThat(minimum).isNull()
        assertThat(maximum).isNull()
    }

    @Test
    fun `latitude returns OutOfRange error when value is above 90`() {
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
    fun `latitude returns OutOfRange error when value is below minus 90`() {
        // Given
        val value = -90.1

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)
    }

    @Test
    fun `latitude returns NonFiniteNumber error for NaN`() {
        // Given
        val value = Double.NaN

        // When
        val result = validator.latitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `longitude returns null for values inside the range including both limits`() {
        // Given
        val middle = 0.0
        val minimum = -180.0
        val maximum = 180.0

        // When
        val middleResult = validator.longitude(middle)
        val minimumResult = validator.longitude(minimum)
        val maximumResult = validator.longitude(maximum)

        // Then
        assertThat(middleResult).isNull()
        assertThat(minimumResult).isNull()
        assertThat(maximumResult).isNull()
    }

    @Test
    fun `longitude returns OutOfRange error when value is above 180`() {
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
    fun `longitude returns OutOfRange error when value is below minus 180`() {
        // Given
        val value = -180.1

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)
    }

    @Test
    fun `longitude returns NonFiniteNumber error for infinity`() {
        // Given
        val value = Double.NEGATIVE_INFINITY

        // When
        val result = validator.longitude(value)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.LONGITUDE)
    }
}