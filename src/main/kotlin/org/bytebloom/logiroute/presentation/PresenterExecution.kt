package org.bytebloom.logiroute.presentation

internal fun Throwable.userMessage(): String = message?.takeIf { it.isNotBlank() }
    ?: "An unexpected error occurred. Please try again."
