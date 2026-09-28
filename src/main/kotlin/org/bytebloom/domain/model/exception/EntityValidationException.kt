package org.bytebloom.domain.model.exception

import org.bytebloom.domain.validator.ValidatorError
import org.bytebloom.domain.validator.describe

class EntityValidationException(val violations: List<ValidatorError>) :
    DomainException("Validation failed: ${violations.joinToString("; ") { it.describe() }}")