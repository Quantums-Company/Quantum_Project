package org.bytebloom.logiroute.presentation

data class SearchTraceSummary(
    val keystrokes: Int,
    val searchesStarted: Int,
    val searchesCancelled: Int,
    val searchesCompleted: Int
)