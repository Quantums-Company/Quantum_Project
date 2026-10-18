package org.bytebloom.logiroute.data.csv.io

import java.io.File

class CsvFileWriter {

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