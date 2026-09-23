package edu.logiroute.logiroute.domain.model

data class Package(
    val id: String,
    val weight: Double,
    val priority: String,
    val originWarehouse: String,
    val destinationWarehouse: String,
)