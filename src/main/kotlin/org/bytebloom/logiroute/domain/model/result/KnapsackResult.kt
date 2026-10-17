package org.bytebloom.logiroute.domain.model.result

import org.bytebloom.logiroute.domain.model.CargoItem

data class KnapsackResult(
    val selectedItems: List<CargoItem>,
    val totalWeightKg: Int,
    val totalPriorityValue: Int,
    val totalVolumeM3: Double = 0.0
)