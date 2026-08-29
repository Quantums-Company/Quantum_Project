package org.bytebloom.domain.performance

data class TreePerformanceReport(
    val totalPackages: Int,
    val trackingId: String,
    val binarySearchTreeSteps: Int,
    val avlTreeSteps: Int
)