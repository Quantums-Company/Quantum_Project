package org.bytebloom.logiroute.data.csv.io

data class CsvLoadReport<T>(val rows: List<T>, val skippedLines: List<String>)
