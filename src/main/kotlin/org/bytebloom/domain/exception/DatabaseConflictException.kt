package org.bytebloom.domain.exception

class DatabaseConflictException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)