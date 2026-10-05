package org.bytebloom.domain.model

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField

enum class Priority(val score: Int) {
    URGENT(score = 3),
    STANDARD(score = 2),
    LOW(score = 1);

    companion object {
        fun from(value: String): Priority =
            when (value.trim().uppercase()) {
                "URGENT" -> URGENT
                "STANDARD" -> STANDARD
                "LOW" -> LOW
                else -> throw EntityValidationException(
                    listOf(ValidatorError.Custom(ValidatorField.PRIORITY, "Unknown priority '$value'"))
                )
            }
    }
}