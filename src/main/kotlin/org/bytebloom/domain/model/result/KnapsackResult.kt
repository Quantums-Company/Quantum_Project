package org.bytebloom.domain.model.result

import org.bytebloom.domain.model.CargoItem

data class KnapsackResult(
    val selectedItems: List<CargoItem>,
    val totalWeightKg: Int,
    val totalPriorityValue: Int,
    val totalVolumeM3: Double = 0.0
)