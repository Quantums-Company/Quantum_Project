package org.bytebloom.logiroute.data.csv.io

import org.bytebloom.logiroute.data.csv.exception.CsvFileNotFoundException
import org.bytebloom.logiroute.data.csv.exception.CsvParsingException
import java.io.File
import java.io.IOException

class CsvFileReader(
private val cacheDirectory: String = CACHE_DIRECTORY,
private val seedDirectory: String = DEFAULT_CSV_DIRECTORY
) {
    companion object {
        const val DEFAULT_CSV_DIRECTORY = "src/resources"
        const val CACHE_DIRECTORY = "src/resources/cache"
    }

    fun <T> loadCsv(
        fileName: String,
        parser: (String, Int) -> T?
    ): CsvLoadReport<T> {
        val cacheFile = File(cacheDirectory, fileName)
        val seedFile = File(seedDirectory, fileName)

        val file = when {
            cacheFile.isFile -> cacheFile
            seedFile.isFile -> seedFile
            else -> throw CsvFileNotFoundException(
                fileName,
                "$cacheDirectory or $seedDirectory"
            )
        }

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
                            skipped.add(
                                e.message
                                    ?: "Line $lineNumber: unknown parsing error"
                            )
                        }
                    }
                }
            }
        } catch (e: IOException) {
            throw CsvParsingException(
                "Failed reading '${file.path}': ${e.message}"
            )
        }

        return CsvLoadReport(rows, skipped)
    }
}