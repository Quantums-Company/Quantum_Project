package org.bytebloom.logiroute.domain.model.exception

class DatabaseConflictException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)