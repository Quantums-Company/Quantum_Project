package org.bytebloom.domain.model.exception

import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.describe

class EntityValidationException(val violations: List<ValidatorError>) :
    DomainException("Validation failed: ${violations.joinToString("; ") { it.describe() }}")