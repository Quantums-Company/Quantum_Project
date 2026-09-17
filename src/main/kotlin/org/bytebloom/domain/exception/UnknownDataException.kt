package org.bytebloom.domain.exception

class UnknownDataException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)