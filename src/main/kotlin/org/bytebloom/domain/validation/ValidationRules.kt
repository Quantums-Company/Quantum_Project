package org.bytebloom.domain.validation

class ValidationRules {

    fun requiredText(
        value: String,
        field: ValidationField
    ): ValidationError? {
        return if (value.isBlank()) {
            ValidationError.Blank(field)
        } else {
            null
        }
    }

    fun positive(
        value: Double,
        field: ValidationField
    ): ValidationError? {

        if (!value.isFinite()) {
            return ValidationError.NonFiniteNumber(field)
        }

        return if (value <= 0.0) {
            ValidationError.NotPositive(field)
        } else {
            null
        }
    }

    fun nonNegative(
        value: Int,
        field: ValidationField
    ): ValidationError? {
        return if (value < 0) {
            ValidationError.NegativeValue(field)
        } else {
            null
        }
    }

    fun latitude(
        value: Double
    ): ValidationError? {

        if (!value.isFinite()) {
            return ValidationError.NonFiniteNumber(
                ValidationField.Latitude()
            )
        }

        return if (value !in -90.0..90.0) {
            ValidationError.OutOfRange(
                field = ValidationField.Latitude(),
                minimum = -90.0,
                maximum = 90.0
            )
        } else {
            null
        }
    }

    fun longitude(
        value: Double
    ): ValidationError? {

        if (!value.isFinite()) {
            return ValidationError.NonFiniteNumber(
                ValidationField.Longitude()
            )
        }

        return if (value !in -180.0..180.0) {
            ValidationError.OutOfRange(
                field = ValidationField.Longitude(),
                minimum = -180.0,
                maximum = 180.0
            )
        } else {
            null
        }
    }
}