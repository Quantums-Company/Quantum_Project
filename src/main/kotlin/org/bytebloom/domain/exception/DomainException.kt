package org.bytebloom.domain.exception

sealed class DomainException(message: String, cause: Throwable? = null) : Exception(message, cause)
