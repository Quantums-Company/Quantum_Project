package org.bytebloom.logiroute.domain.model.exception

sealed class DomainException(message: String, cause: Throwable? = null) : Exception(message, cause)
