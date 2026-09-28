package org.bytebloom.domain.validator

class FieldValidator {

    fun requiredText(
        value: String,
        field: ValidatorField
    ): ValidatorError? {
        return if (value.isBlank()) {
            ValidatorError.Blank(field)
        } else {
            null
        }
    }

    fun positive(
        value: Double,
        field: ValidatorField
    ): ValidatorError? {

        if (!value.isFinite()) {
            return ValidatorError.NonFiniteNumber(field)
        }

        return if (value <= 0.0) {
            ValidatorError.NotPositive(field)
        } else {
            null
        }
    }

    fun nonNegative(
        value: Int,
        field: ValidatorField
    ): ValidatorError? {
        return if (value < 0) {
            ValidatorError.NegativeValue(field)
        } else {
            null
        }
    }

    fun latitude(
        value: Double
    ): ValidatorError? {

        if (!value.isFinite()) {
            return ValidatorError.NonFiniteNumber(
                ValidatorField.Latitude()
            )
        }

        return if (value !in -90.0..90.0) {
            ValidatorError.OutOfRange(
                field = ValidatorField.Latitude(),
                minimum = -90.0,
                maximum = 90.0
            )
        } else {
            null
        }
    }

    fun longitude(
        value: Double
    ): ValidatorError? {

        if (!value.isFinite()) {
            return ValidatorError.NonFiniteNumber(
                ValidatorField.Longitude()
            )
        }

        return if (value !in -180.0..180.0) {
            ValidatorError.OutOfRange(
                field = ValidatorField.Longitude(),
                minimum = -180.0,
                maximum = 180.0
            )
        } else {
            null
        }
    }
}