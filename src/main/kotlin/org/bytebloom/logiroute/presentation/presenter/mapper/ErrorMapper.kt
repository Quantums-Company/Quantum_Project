package org.bytebloom.logiroute.presentation.presenter.mapper

import org.bytebloom.logiroute.domain.model.exception.DatabaseConflictException
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.presentation.model.UiError

internal fun Throwable.toUiError(): UiError {
    val detail = message ?: NO_DETAIL
    return when (this) {
        is NetworkUnavailableException -> UiError.Network(detail)
        is ResourceNotFoundException -> UiError.NotFound(detail)
        is DatabaseConflictException -> UiError.Conflict(detail)
        is EntityValidationException -> UiError.InvalidInput(detail)
        else -> UiError.Unexpected(detail)
    }
}

private const val NO_DETAIL = "No further details are available."
