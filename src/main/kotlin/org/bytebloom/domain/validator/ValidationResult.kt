package org.bytebloom.domain.validator

sealed class ValidationResult {
    data class Valid(
        val message: String = ""
    ) : ValidationResult()

    data class Invalid(
        val violations: List<String>
    ) : ValidationResult()
}