package org.bytebloom.domain.model.exception

import org.bytebloom.domain.validator.FieldViolation
import org.bytebloom.domain.validator.describe

class EntityValidationException(val violations: List<FieldViolation>) :
    DomainException("Validation failed: ${violations.joinToString("; ") { it.describe() }}")