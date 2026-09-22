package org.bytebloom.domain.model.exception

class NetworkUnavailableException(
    message: String = "Could not reach the remote service — check your internet connection.",
    cause: Throwable? = null
) : DomainException(message, cause)