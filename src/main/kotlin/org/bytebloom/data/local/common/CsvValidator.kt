// data/local/common/CsvRowRules.kt
package org.bytebloom.data.local.common

import org.bytebloom.data.exception.CsvParsingException

fun requireExpectedColumnCount(columns: List<String>, expected: Int, lineNumber: Int) {
    if (columns.size != expected) {
        throw CsvParsingException("Line $lineNumber: expected $expected columns but found ${columns.size}.")
    }
}

fun requireNonBlankValues(lineNumber: Int, message: String, vararg values: String) {
    if (values.any(String::isBlank)) {
        throw CsvParsingException("Line $lineNumber: $message")
    }
}

fun String?.toDoubleOrThrow(field: String, line: Int): Double? {
    if (this.isNullOrBlank() || this.equals("null", ignoreCase = true)) return null
    return this.toDoubleOrNull() ?: throw CsvParsingException("Line $line: invalid $field '$this'.")
}

fun String?.toIntOrThrow(field: String, line: Int): Int? {
    if (this.isNullOrBlank() || this.equals("null", ignoreCase = true)) return null
    return this.toIntOrNull() ?: throw CsvParsingException("Line $line: invalid $field '$this'.")
}