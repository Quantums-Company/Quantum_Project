package org.bytebloom.logiroute.data.remote.exception

class CallNotPermittedException(
    message: String = "Circuit breaker is OPEN; remote call is not permitted."
) : RuntimeException(message)