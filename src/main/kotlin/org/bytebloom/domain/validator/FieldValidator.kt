package org.bytebloom.domain.validator

import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField

class FieldValidator {
    companion object {
        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0
    }

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
                ValidatorField.LATITUDE
            )
        }

        return if (value !in MIN_LATITUDE..MAX_LATITUDE) {
            ValidatorError.OutOfRange(
                field = ValidatorField.LATITUDE,
                minimum = MIN_LATITUDE,
                maximum = MAX_LATITUDE
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
                ValidatorField.LONGITUDE
            )
        }

        return if (value !in MIN_LONGITUDE..MAX_LONGITUDE) {
            ValidatorError.OutOfRange(
                field = ValidatorField.LONGITUDE,
                minimum = MIN_LONGITUDE,
                maximum = MAX_LONGITUDE
            )
        } else {
            null
        }
    }
}