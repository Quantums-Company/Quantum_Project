package org.bytebloom.data.raw

import org.bytebloom.util.Logger

enum class Priority {
    URGENT,
    STANDARD,
    LOW;

    companion object {

        fun from(value: String): Priority =
            when(value.trim().uppercase()) {
                "URGENT" -> URGENT
                "STANDARD" -> STANDARD
                "LOW" -> LOW
                else -> {
                    Logger.warning("Unknown priority '$value'. Using LOW.")
                    LOW
                }

            }
        }

}