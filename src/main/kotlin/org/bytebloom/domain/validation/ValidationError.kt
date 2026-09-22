package org.bytebloom.domain.validation

sealed class ValidationError(
    val field: ValidationField
) {

    class Blank(
        field: ValidationField
    ) : ValidationError(field)

    class InvalidIdFormat(
        field: ValidationField,
        val entityType: EntityType
    ) : ValidationError(field)

    class NotPositive(
        field: ValidationField
    ) : ValidationError(field)

    class NegativeValue(
        field: ValidationField
    ) : ValidationError(field)

    class OutOfRange(
        field: ValidationField,
        val minimum: Double,
        val maximum: Double
    ) : ValidationError(field)

    class NonFiniteNumber(
        field: ValidationField
    ) : ValidationError(field)

    class NoFieldsProvided(
        field: ValidationField.Entity
    ) : ValidationError(field)

    class SameWarehouse(
        field: ValidationField
    ) : ValidationError(field)

    class Custom(
        field: ValidationField,
        val message: String
    ) : ValidationError(field)
}

fun ValidationError.describe(): String = when (this) {
    is ValidationError.Blank -> "${field.displayName()} cannot be blank"
    is ValidationError.InvalidIdFormat ->
        "${field.displayName()} must match ${entityType.idPrefix}<number or UUID>"
    is ValidationError.NotPositive -> "${field.displayName()} must be greater than 0"
    is ValidationError.NegativeValue -> "${field.displayName()} cannot be negative"
    is ValidationError.OutOfRange -> "${field.displayName()} must be between $minimum and $maximum"
    is ValidationError.NonFiniteNumber -> "${field.displayName()} must be a finite number"
    is ValidationError.NoFieldsProvided -> "At least one field must be provided to update"
    is ValidationError.SameWarehouse -> "${field.displayName()} must differ from the other warehouse"
    is ValidationError.Custom -> message
}