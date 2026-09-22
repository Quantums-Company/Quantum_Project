package org.bytebloom.domain.validation

fun List<ValidationError>.toValidationResult(): ValidationResult =
    if (isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(this)