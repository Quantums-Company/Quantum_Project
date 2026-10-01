package org.bytebloom.domain.knapsack

import org.bytebloom.domain.model.CargoItem
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.result.KnapsackResult
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField

class KnapsackCargoOptimizer {
    operator fun invoke(items: List<CargoItem>, capacityKg: Int): KnapsackResult {
        if (capacityKg < 0) {
            throw EntityValidationException(listOf(ValidatorError.NegativeValue(ValidatorField.MAX_CAPACITY_KG)))
        }
        items.forEach { item ->
            if (item.weightKg < 0) {
                throw EntityValidationException(listOf(ValidatorError.NegativeValue(ValidatorField.WEIGHT)))
            }
        }

        val itemCount = items.size
        val grid = Array(itemCount + 1) { IntArray(capacityKg + 1) }

        for (i in 1..itemCount) {
            val item = items[i - 1]
            for (w in 0..capacityKg) {
                grid[i][w] = if (item.weightKg <= w) {
                    maxOf(grid[i - 1][w], grid[i - 1][w - item.weightKg] + item.priorityValue)
                } else {
                    grid[i - 1][w]
                }
            }
        }

        val selected = mutableListOf<CargoItem>()
        var remainingCapacity = capacityKg
        for (i in itemCount downTo 1) {
            if (grid[i][remainingCapacity] != grid[i - 1][remainingCapacity]) {
                val item = items[i - 1]
                selected.add(item)
                remainingCapacity -= item.weightKg
            }
        }
        selected.reverse()

        return KnapsackResult(
            selectedItems = selected,
            totalWeightKg = selected.sumOf { it.weightKg },
            totalPriorityValue = grid[itemCount][capacityKg]
        )
    }
}