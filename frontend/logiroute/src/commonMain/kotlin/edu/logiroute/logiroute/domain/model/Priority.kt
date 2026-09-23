package edu.logiroute.logiroute.domain.model

import edu.logiroute.logiroute.domain.exception.InvalidPriorityException
import edu.logiroute.logiroute.domain.exception.MissingPriorityException

enum class Priority {
    URGENT,
    STANDARD,
    LOW;
    companion object {
        fun from(value: String?): Priority {
            if (value.isNullOrBlank()) {
                throw MissingPriorityException()
            }

            return when (val normalized = value.trim().uppercase()) {
                "URGENT","CRITICAL" -> URGENT
                "STANDARD" -> STANDARD
                "LOW" -> LOW
                else -> throw InvalidPriorityException(normalized)
            }
        }
    }
}
