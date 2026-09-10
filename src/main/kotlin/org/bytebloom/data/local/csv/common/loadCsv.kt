package org.bytebloom.data.local.csv.common

import org.bytebloom.util.Logger
import java.io.File
import java.io.IOException

const val DEFAULT_CSV_DIRECTORY = "src/resources"

fun <T> loadCsv(
    csvDirectory: String = DEFAULT_CSV_DIRECTORY,
    fileName: String,
    parser: (String, Int) -> T?
): List<T> {
    val raws = mutableListOf<T>()

    loadCsvFile(csvDirectory, fileName) { line, lineNumber ->
        parser(line, lineNumber)?.let(raws::add)
    }

    Logger.info("Successfully parsed $fileName: ${raws.size} row(s).")
    return raws
}

private fun loadCsvFile(
    csvDirectory: String,
    fileName: String,
    processLine: (String, Int) -> Unit
) {
    val file = File(csvDirectory, fileName)

    if (!file.exists()) {
        Logger.warning("File '$fileName' was not found in '$csvDirectory'.")
        return
    }

    try {
        file.useLines { lines ->
            lines.drop(1).forEachIndexed { index, line ->
                if (line.isNotBlank()) {
                    processLine(line, index + 2)
                }
            }
        }
    } catch (e: IOException) {
        Logger.error("reading file '$fileName': ${e.message}")
    }
}