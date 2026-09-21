package org.bytebloom.domain.model.exception

class NetworkUnavailableException(
    message: String = "Could not reach Supabase — check your internet connection.",
    cause: Throwable? = null
) :DomainException(message, cause)