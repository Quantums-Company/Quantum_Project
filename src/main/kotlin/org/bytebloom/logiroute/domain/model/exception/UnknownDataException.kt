package org.bytebloom.logiroute.domain.model.exception

class UnknownDataException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)