package org.bytebloom.data.raw

enum class Priority {
    URGENT,
    STANDARD,
    LOW;

    companion object {

        fun from(value: String): Priority {

            return when(value.trim().uppercase()) {

                "URGENT" -> URGENT

                "STANDARD" -> STANDARD

                else -> LOW
            }
        }
    }
}