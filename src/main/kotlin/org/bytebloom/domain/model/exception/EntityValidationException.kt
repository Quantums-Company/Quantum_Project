package org.bytebloom.domain.model.exception

import org.bytebloom.domain.validation.ValidationError
import org.bytebloom.domain.validation.describe

class EntityValidationException(val violations: List<ValidationError>) :
    DomainException("Validation failed: ${violations.joinToString("; ") { it.describe() }}")