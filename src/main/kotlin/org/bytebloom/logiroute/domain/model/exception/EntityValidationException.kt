package org.bytebloom.logiroute.domain.model.exception

import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.describe

class EntityValidationException(val violations: List<ValidatorError>) :
    DomainException("Validation failed: ${violations.joinToString("; ") { it.describe() }}")