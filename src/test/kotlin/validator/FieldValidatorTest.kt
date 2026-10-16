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
        val text = "Main Hub"

        val result = validator.requiredText(text, ValidatorField.NAME)

        assertThat(result).isNull()
    }

    @Test
    fun `requiredText returns Blank error type when text is empty`() {
        val text = ""

        val result = validator.requiredText(text, ValidatorField.NAME)

        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
    }

    @Test
    fun `requiredText sets target field when text is empty`() {
        val text = ""

        val result = validator.requiredText(text, ValidatorField.NAME)

        assertThat(result?.field).isEqualTo(ValidatorField.NAME)
    }

    @Test
    fun `requiredText returns Blank error type when text contains only spaces`() {
        val text = "   "

        val result = validator.requiredText(text, ValidatorField.REGIONAL_ZONE)

        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
    }

    @Test
    fun `requiredText returns Blank error when text contains only spaces`() {
        val text = "   "

        val result = validator.requiredText(text, ValidatorField.REGIONAL_ZONE)

        assertThat(result?.field).isEqualTo(ValidatorField.REGIONAL_ZONE)
    }

    @Test
    fun `positive returns null when value is greater than zero`() {
        val value = 5.0

        val result = validator.positive(value, ValidatorField.WEIGHT)

        assertThat(result).isNull()
    }

    @Test
    fun `positive returns NotPositive error type when value is zero`() {
        val value = 0.0

        val result = validator.positive(value, ValidatorField.WEIGHT)

        assertThat(result).isInstanceOf(ValidatorError.NotPositive::class.java)
    }

    @Test
    fun `positive sets target field when value is zero`() {
        val value = 0.0

        val result = validator.positive(value, ValidatorField.WEIGHT)

        assertThat(result?.field).isEqualTo(ValidatorField.WEIGHT)
    }

    @Test
    fun `positive returns NotPositive error when value is negative`() {
        val value = -3.5

        val result = validator.positive(value, ValidatorField.WEIGHT)

        assertThat(result).isInstanceOf(ValidatorError.NotPositive::class.java)
    }

    @Test
    fun `positive returns NonFiniteNumber error for NaN`() {
        val notANumber = Double.NaN

        val nanResult = validator.positive(notANumber, ValidatorField.WEIGHT)

        assertThat(nanResult).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
    }

    @Test
    fun `positive returns NonFiniteNumber error for infinity`() {
        val infinity = Double.POSITIVE_INFINITY

        val infinityResult = validator.positive(infinity, ValidatorField.WEIGHT)

        assertThat(infinityResult).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
    }

    @Test
    fun `nonNegative returns null for zero values`() {
        val zero = 0

        val zeroResult = validator.nonNegative(zero, ValidatorField.DISTANCE_KM)

        assertThat(zeroResult).isNull()
    }

    @Test
    fun `nonNegative returns null for positive values`() {
        val positive = 10

        val positiveResult = validator.nonNegative(positive, ValidatorField.DISTANCE_KM)

        assertThat(positiveResult).isNull()
    }

    @Test
    fun `nonNegative returns NegativeValue error when value is below zero`() {
        val value = -1

        val result = validator.nonNegative(value, ValidatorField.DISTANCE_KM)

        assertThat(result).isInstanceOf(ValidatorError.NegativeValue::class.java)
    }

    @Test
    fun `nonNegative sets target field when value is below zero`() {
        val value = -1

        val result = validator.nonNegative(value, ValidatorField.DISTANCE_KM)

        assertThat(result?.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @Test
    fun `latitude returns null for values inside is at minimum limit`() {
        val min = -90.0

        val minimum = validator.latitude(min)

        assertThat(minimum).isNull()
    }

    @Test
    fun `latitude returns null for values inside is at middle limit`() {
        val mid = 0.0

        val middle = validator.latitude(mid)

        assertThat(middle).isNull()
    }

    @Test
    fun `latitude returns null for values inside is at maximum limit`() {
        val max = 90.0

       val maximum = validator.latitude(max)

        assertThat(maximum).isNull()
    }

    @Test
    fun `latitude returns OutOfRange error when value is above 90`() {
        val value = 90.1

        val result = validator.latitude(value)

        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)
    }

@Test
fun `latitude sets LATITUDE field when value is above 90`() {
    val value = 90.1

    val result = validator.latitude(value) as ValidatorError.OutOfRange

    assertThat(result.field).isEqualTo(ValidatorField.LATITUDE)
}

@Test
fun `latitude sets correct minimum bound in error when value is above 90`() {
    val value = 90.1

    val result = validator.latitude(value) as ValidatorError.OutOfRange

    assertThat(result.minimum).isEqualTo(-90.0)
}

@Test
fun `latitude sets correct maximum bound in error when value is above 90`() {
    val value = 90.1

    val result = validator.latitude(value) as ValidatorError.OutOfRange

    assertThat(result.maximum).isEqualTo(90.0)
}

    @Test
    fun `latitude returns OutOfRange error when value is below minus 90`() {
        val value = -90.1

        val result = validator.latitude(value)

        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)
    }

    @Test
    fun `latitude returns NonFiniteNumber error type for NaN`() {
        val value = Double.NaN

        val result = validator.latitude(value)

        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
    }

    @Test
    fun `latitude sets LATITUDE field when value is NaN`() {
        val value = Double.NaN

        val result = validator.latitude(value)

        assertThat(result?.field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `longitude returns null when values is at minimum limit`() {
        val minimum = -180.0

        val minimumResult = validator.longitude(minimum)

        assertThat(minimumResult).isNull()
    }

    @Test
    fun `longitude returns null when values is at middle limit`() {
        val middle = 0.0

        val middleResult = validator.longitude(middle)

        assertThat(middleResult).isNull()
    }

    @Test
    fun `longitude returns null when values is at maximum limit`() {
        val maximum = 180.0

        val maximumResult = validator.longitude(maximum)

        assertThat(maximumResult).isNull()
    }

    @Test
    fun `longitude returns OutOfRange error when value is above 180`() {
        val value = 180.1

        val result = validator.longitude(value)

        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)
    }

@Test
fun `longitude sets LONGITUDE field when value is above 180`() {
    val value = 180.1

    val result = validator.longitude(value) as ValidatorError.OutOfRange

    assertThat(result.field).isEqualTo(ValidatorField.LONGITUDE)
}

@Test
fun `longitude sets correct minimum bound in error when value is above 180`() {
    val value = 180.1

    val result = validator.longitude(value) as ValidatorError.OutOfRange

    assertThat(result.minimum).isEqualTo(-180.0)
}

@Test
fun `longitude sets correct maximum bound in error when value is above 180`() {
    val value = 180.1

    val result = validator.longitude(value) as ValidatorError.OutOfRange

    assertThat(result.maximum).isEqualTo(180.0)
}

    @Test
    fun `longitude returns OutOfRange error when value is below minus 180`() {
        val value = -180.1

        val result = validator.longitude(value)

        assertThat(result).isInstanceOf(ValidatorError.OutOfRange::class.java)
    }

    @Test
    fun `longitude returns NonFiniteNumber error type for negative infinity`() {
        val value = Double.NEGATIVE_INFINITY

        val result = validator.longitude(value)

        assertThat(result).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
    }

    @Test
    fun `longitude sets LONGITUDE field when value is negative infinity`() {
        val value = Double.NEGATIVE_INFINITY

        val result = validator.longitude(value)

        assertThat(result?.field).isEqualTo(ValidatorField.LONGITUDE)
    }
}