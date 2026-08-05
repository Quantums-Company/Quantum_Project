package org.bytebloom.data.csv

import org.bytebloom.util.Logger

fun hasExpectedColumns(
    columns: List<String>,
    expected: Int,
    lineNumber: Int
): Boolean {
    if (columns.size != expected) {
        Logger.warning(
            "Skipping line $lineNumber. " +
                    "Expected $expected columns but found ${columns.size}."
        )
        return false
    }

    return true
}

fun hasRequiredValues(
    lineNumber: Int,
    message: String,
    vararg values: String
): Boolean {
    if (values.any(String::isBlank)) {
        Logger.warning(
            "Skipping line $lineNumber. " +
                    message
        )
        return false
    }

    return true
}

fun String?.toValidDouble(
    field: String,
    line: Int
): Double? {
    if (this.isNullOrBlank() || this.equals("null", ignoreCase = true)) {
        return null
    }

    return this.toDoubleOrNull() ?: run {
        Logger.warning(
            "Skipping line $line. " +
                    "Invalid $field '$this'."
        )
        null
    }
}

fun String?.toValidInteger(
    field: String,
    line: Int
): Int? {
    if (this.isNullOrBlank() || this.equals("null", ignoreCase = true)) {
        return null
    }

    return this.toIntOrNull() ?: run {
        Logger.warning(
            "Skipping line $line. " +
                    "Invalid $field '$this'."
        )
        null
    }
}