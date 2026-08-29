package org.bytebloom.domain.performance

data class TreePerformanceReport(
    val totalPackages: Int,
    val results: List<TreeSearchResult>
)