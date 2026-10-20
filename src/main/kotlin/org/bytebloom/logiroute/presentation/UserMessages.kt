package org.bytebloom.logiroute.presentation

import org.bytebloom.logiroute.domain.model.exception.DatabaseConflictException
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.IllegalShipmentTransitionException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.exception.UnknownDataException

private const val NO_DETAIL = "No further details are available."
private const val CONFLICT_PREFIX = "Conflict with existing data: "
private const val UNKNOWN_DATA_PREFIX = "The stored data could not be understood: "
private const val UNEXPECTED_PREFIX = "Something unexpected happened: "

/**
 * Turns an exception into a sentence a person can read.
 * Every typed domain exception has its own branch; nothing is decided by comparing strings.
 */
internal fun Exception.toUserMessage(): String {
    val detail = message ?: NO_DETAIL
    return when (this) {
        is NetworkUnavailableException -> detail
        is ResourceNotFoundException -> detail
        is EntityValidationException -> detail
        is IllegalShipmentTransitionException -> detail
        is DatabaseConflictException -> CONFLICT_PREFIX + detail
        is UnknownDataException -> UNKNOWN_DATA_PREFIX + detail
        is IllegalArgumentException -> detail
        else -> UNEXPECTED_PREFIX + detail
    }
}
