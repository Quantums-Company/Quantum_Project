package org.bytebloom.domain.validator

import org.bytebloom.domain.model.EntityType

sealed class ValidatorError(
    val field: ValidatorField
) {

    class Blank(
        field: ValidatorField
    ) : ValidatorError(field)

    class InvalidIdFormat(
        field: ValidatorField,
        val entityType: EntityType
    ) : ValidatorError(field)

    class NotPositive(
        field: ValidatorField
    ) : ValidatorError(field)

    class NegativeValue(
        field: ValidatorField
    ) : ValidatorError(field)

    class OutOfRange(
        field: ValidatorField,
        val minimum: Double,
        val maximum: Double
    ) : ValidatorError(field)

    class NonFiniteNumber(
        field: ValidatorField
    ) : ValidatorError(field)

    class NoFieldsProvided(
        field: ValidatorField
    ) : ValidatorError(field)

    class SameWarehouse(
        field: ValidatorField
    ) : ValidatorError(field)

    class Custom(
        field: ValidatorField,
        val message: String
    ) : ValidatorError(field)
}

fun ValidatorError.describe(): String = when (this) {
    is ValidatorError.Blank -> "${field.displayName} cannot be blank"
    is ValidatorError.InvalidIdFormat ->
        "${field.displayName} must match ${entityType.idPrefix}<number or UUID>"
    is ValidatorError.NotPositive -> "${field.displayName} must be greater than 0"
    is ValidatorError.NegativeValue -> "${field.displayName} cannot be negative"
    is ValidatorError.OutOfRange -> "${field.displayName} must be between $minimum and $maximum"
    is ValidatorError.NonFiniteNumber -> "${field.displayName} must be a finite number"
    is ValidatorError.NoFieldsProvided -> "At least one field must be provided to update"
    is ValidatorError.SameWarehouse -> "${field.displayName} must differ from the other warehouse"
    is ValidatorError.Custom -> message
}