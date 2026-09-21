package org.bytebloom.domain.model.exception

class DatabaseConflictException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)