package org.bytebloom.domain.exception

class EntityValidationException(
    val violations: List<String>
) : DomainException("Validation failed: ${violations.joinToString("; ")}")
