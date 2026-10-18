package org.bytebloom.logiroute.presentation.model

/**
 * What went wrong, in a form every UI can render in its own way.
 * The presenter decides the *kind* of problem; the UI decides the wording, colours and icons.
 */
sealed interface UiError {
    val detail: String

    data class Network(override val detail: String) : UiError
    data class NotFound(override val detail: String) : UiError
    data class Conflict(override val detail: String) : UiError
    data class InvalidInput(override val detail: String) : UiError
    data class Unexpected(override val detail: String) : UiError
}
