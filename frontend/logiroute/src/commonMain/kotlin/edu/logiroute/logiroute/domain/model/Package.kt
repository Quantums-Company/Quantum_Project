package edu.logiroute.logiroute.domain.model

data class Package(
    val id: String,
    val weight: Double,
    val priority: Priority,
    val originWarehouse: String,
    val destinationWarehouse: String,
)