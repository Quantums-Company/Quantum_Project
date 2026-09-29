package edu.logiroute.logiroute.domain.exception

open class PriorityException(message: String) : Exception(message) {

    companion object {
        const val ERROR_MISSING_PRIORITY = "Validation Failed: Cargo priority value cannot be null or blank."
        const val ERROR_INVALID_PRIORITY_TEMPLATE = "Validation Failed: '%s' is an unknown or unsupported cargo priority."
    }
}

class MissingPriorityException :
    PriorityException(ERROR_MISSING_PRIORITY)

class InvalidPriorityException(val invalidValue: String) :
    PriorityException(ERROR_INVALID_PRIORITY_TEMPLATE.format(invalidValue))