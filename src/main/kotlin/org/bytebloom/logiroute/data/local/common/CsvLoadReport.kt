package org.bytebloom.logiroute.data.local.common

data class CsvLoadReport<T>(val rows: List<T>, val skippedLines: List<String>)
