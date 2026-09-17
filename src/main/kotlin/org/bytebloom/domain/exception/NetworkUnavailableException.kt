package org.bytebloom.domain.exception

class NetworkUnavailableException(
    message: String = "Could not reach Supabase — check your internet connection.",
    cause: Throwable? = null
) :DomainException(message, cause)