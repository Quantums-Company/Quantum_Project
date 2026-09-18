package org.bytebloom.data.local.cleaner

import java.io.File

class CsvWriter {

    fun write(
        file: File,
        header: String,
        rows: List<String>
    ) {
        file.parentFile?.mkdirs()

        file.bufferedWriter().use { writer ->
            writer.write(header)
            writer.newLine()

            rows.forEach { row ->
                writer.write(row)
                writer.newLine()
            }
        }
    }
}