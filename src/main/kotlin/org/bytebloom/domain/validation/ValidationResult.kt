package org.bytebloom.domain.validation

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val violations: List<ValidationError>) : ValidationResult()
}