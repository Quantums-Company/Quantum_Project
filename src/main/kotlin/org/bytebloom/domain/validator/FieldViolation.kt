package org.bytebloom.domain.validator

sealed class FieldViolation(val field: String) {
    data class BlankField(val fieldName: String) : FieldViolation(fieldName)
    data class InvalidPrefix(val fieldName: String, val expectedPrefix: String) : FieldViolation(fieldName)
    data class OutOfRange(val fieldName: String, val min: Double, val max: Double) : FieldViolation(fieldName)
    data class NotPositive(val fieldName: String) : FieldViolation(fieldName)
    data class NoFieldsProvided(val entityName: String) : FieldViolation(entityName)

    data class CustomError(val fieldName: String, val customMessage: String) : FieldViolation(fieldName)
}

fun FieldViolation.describe(): String = when (this) {
    is FieldViolation.BlankField -> "$field cannot be blank"
    is FieldViolation.InvalidPrefix -> "$field must start with $expectedPrefix"
    is FieldViolation.OutOfRange -> "$field must be between $min and $max"
    is FieldViolation.NotPositive -> "$field must be greater than 0"
    is FieldViolation.NoFieldsProvided -> "At least one field must be provided to update $entityName"
    is FieldViolation.CustomError -> customMessage
}