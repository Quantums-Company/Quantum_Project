package org.bytebloom.logiroute.domain.performance

data class TreeSearchResult(
    val trackingId: String,
    val binarySearchTreeSteps: Int,
    val avlTreeSteps: Int
)