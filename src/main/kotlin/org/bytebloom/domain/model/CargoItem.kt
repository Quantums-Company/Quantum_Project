package org.bytebloom.domain.model

data class CargoItem(
    val id: String,
    val weightKg: Int,
    val priorityValue: Int,
    val volumeM3: Double = 0.0
)