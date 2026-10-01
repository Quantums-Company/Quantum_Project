package org.bytebloom.domain.knapsack

import org.bytebloom.domain.model.CargoItem
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.result.KnapsackResult
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField

class KnapsackCargoOptimizer {

    operator fun invoke(items: List<CargoItem>, capacityKg: Int): KnapsackResult {
        validateInputs(items, capacityKg)

        val grid = buildValueGrid(items, capacityKg)
        val selected = backtrackSelectedItems(items, grid, capacityKg)

        return KnapsackResult(
            selectedItems = selected,
            totalWeightKg = selected.sumOf { it.weightKg },
            totalPriorityValue = grid[items.size][capacityKg]
        )
    }

    private fun validateInputs(items: List<CargoItem>, capacityKg: Int) {
        if (capacityKg < 0) {
            throw EntityValidationException(listOf(ValidatorError.NegativeValue(ValidatorField.MAX_CAPACITY_KG)))
        }
        if (items.any { it.weightKg < 0 }) {
            throw EntityValidationException(listOf(ValidatorError.NegativeValue(ValidatorField.WEIGHT)))
        }
    }

    private fun buildValueGrid(items: List<CargoItem>, capacityKg: Int): Array<IntArray> {
        val grid = Array(items.size + 1) { IntArray(capacityKg + 1) }

        for (i in 1..items.size) {
            val item = items[i - 1]
            for (w in 0..capacityKg) {
                grid[i][w] = if (item.weightKg <= w) {
                    maxOf(grid[i - 1][w], grid[i - 1][w - item.weightKg] + item.priorityValue)
                } else {
                    grid[i - 1][w]
                }
            }
        }
        return grid
    }

    private fun backtrackSelectedItems(items: List<CargoItem>, grid: Array<IntArray>, capacityKg: Int): List<CargoItem> {
        val selected = mutableListOf<CargoItem>()
        var remainingCapacity = capacityKg

        for (i in items.size downTo 1) {
            if (grid[i][remainingCapacity] != grid[i - 1][remainingCapacity]) {
                val item = items[i - 1]
                selected.add(item)
                remainingCapacity -= item.weightKg
            }
        }
        return selected.reversed()
    }
}