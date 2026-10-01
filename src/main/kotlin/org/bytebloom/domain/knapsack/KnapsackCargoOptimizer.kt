package org.bytebloom.domain.knapsack

import org.bytebloom.domain.model.CargoItem
import org.bytebloom.domain.model.result.KnapsackResult

class KnapsackCargoOptimizer {
    operator fun invoke(items: List<CargoItem>, capacityKg: Int): KnapsackResult =
        KnapsackResult(selectedItems = emptyList(), totalWeightKg = 0, totalPriorityValue = 0)
}