package org.bytebloom.domain.validator

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val violations: List<FieldViolation>) : ValidationResult()
}