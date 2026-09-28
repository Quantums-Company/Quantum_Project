package org.bytebloom.domain.validator

sealed class ValidatorResult {
    object Valid : ValidatorResult()
    data class Invalid(val violations: List<ValidatorError>) : ValidatorResult()
}
fun List<ValidatorError>.toValidatorResult(): ValidatorResult =
    if (isEmpty()) ValidatorResult.Valid else ValidatorResult.Invalid(this)
