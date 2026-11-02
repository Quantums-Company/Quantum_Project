package org.bytebloom.logiroute.data.csv.io

/**
 * Splits one CSV line into its columns. Understands the quoting [org.bytebloom.logiroute.data.csv.cache.CsvSnapshotStore] writes:
 * a value that contains a comma or a quote is wrapped in quotes and an inner quote is doubled.
 * A comma inside quotes therefore stays inside its column.
 */
class CsvLineSplitter {

    fun split(line: String): List<String> {
        val columns = mutableListOf<String>()
        val current = StringBuilder()
        var insideQuotes = false
        var index = 0

        while (index < line.length) {
            when (val character = line[index]) {
                QUOTE if insideQuotes && line.getOrNull(index + 1) == QUOTE -> {
                    current.append(QUOTE)
                    index++
                }
                QUOTE -> insideQuotes = !insideQuotes
                DELIMITER if !insideQuotes -> {
                    columns.add(current.toString())
                    current.clear()
                }
                else -> current.append(character)
            }
            index++
        }
        columns.add(current.toString())
        return columns
    }

    private companion object {
        const val DELIMITER = ','
        const val QUOTE = '"'
    }
}
