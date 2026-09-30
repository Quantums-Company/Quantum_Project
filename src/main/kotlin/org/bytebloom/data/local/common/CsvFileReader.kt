package org.bytebloom.data.local.common

import org.bytebloom.data.exception.CsvFileNotFoundException
import org.bytebloom.data.exception.CsvParsingException
import java.io.File
import java.io.IOException

class CsvFileReader {
    companion object {
        const val DEFAULT_CSV_DIRECTORY = "src/resources"
    }

    @Suppress("SwallowedException")
    fun <T> loadCsv(
        csvDirectory: String = DEFAULT_CSV_DIRECTORY,
        fileName: String,
        parser: (String, Int) -> T?
    ): CsvLoadReport<T> {
        val file = File(csvDirectory, fileName)
        if (!file.exists()) throw CsvFileNotFoundException(fileName, csvDirectory)

        val rows = mutableListOf<T>()
        val skipped = mutableListOf<String>()

        try {
            file.useLines { lines ->
                lines.drop(1).forEachIndexed { index, line ->
                    val lineNumber = index + 2
                    if (line.isNotBlank()) {
                        try {
                            parser(line, lineNumber)?.let(rows::add)
                        } catch (e: CsvParsingException) {
                            skipped.add(e.message ?: "Line $lineNumber: unknown parsing error")
                        }
                    }
                }
            }
        } catch (e: IOException) {
            throw CsvParsingException("Failed reading '$fileName': ${e.message}")
        }

        return CsvLoadReport(rows, skipped)
    }
}