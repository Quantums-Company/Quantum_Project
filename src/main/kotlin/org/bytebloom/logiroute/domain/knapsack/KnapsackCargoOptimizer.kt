package org.bytebloom.logiroute.domain.knapsack

import org.bytebloom.logiroute.domain.model.CargoItem
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.result.KnapsackResult
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import java.math.BigDecimal
import java.math.RoundingMode

class KnapsackCargoOptimizer {

    companion object {
        private const val VOLUME_SCALE = 100
    }

    operator fun invoke(
        items: List<CargoItem>,
        capacityKg: Int
    ): KnapsackResult {
        validateWeightInputs(items, capacityKg)

        val grid = buildValueGrid(items = items, capacityKg = capacityKg)
        val selected = backtrackSelectedItems(items = items, grid = grid, capacityKg = capacityKg)

        return KnapsackResult(
            selectedItems = selected,
            totalWeightKg = selected.sumOf { it.weightKg },
            totalPriorityValue = grid[items.size][capacityKg],
            totalVolumeM3 = selected.sumOf { it.volumeM3 }
        )
    }

    operator fun invoke(
        items: List<CargoItem>,
        weightCapacityKg: Int,
        volumeCapacityM3: Double
    ): KnapsackResult {
        validateTwoDimensionalInputs(
            items = items,
            weightCapacityKg = weightCapacityKg,
            volumeCapacityM3 = volumeCapacityM3
        )

        val volumeCapacityUnits = toVolumeUnits(volumeCapacityM3)

        val grid = buildTwoDimensionalValueGrid(
            items = items,
            weightCapacityKg = weightCapacityKg,
            volumeCapacityUnits = volumeCapacityUnits
        )

        val selected = backtrackTwoDimensionalItems(
            items = items,
            grid = grid,
            weightCapacityKg = weightCapacityKg,
            volumeCapacityUnits = volumeCapacityUnits
        )

        return KnapsackResult(
            selectedItems = selected,
            totalWeightKg = selected.sumOf { it.weightKg },
            totalPriorityValue = selected.sumOf { it.priorityValue },
            totalVolumeM3 = selected.sumOf { it.volumeM3 }
        )
    }

    private fun validateWeightInputs(
        items: List<CargoItem>,
        capacityKg: Int
    ) {
        val violations = buildList {
            if (capacityKg < 0) {
                add(ValidatorError.NegativeValue(ValidatorField.MAX_CAPACITY_KG))
            }
            if (items.any { it.weightKg < 0 }) {
                add(ValidatorError.NegativeValue(ValidatorField.WEIGHT))
            }
        }

        if (violations.isNotEmpty()) {
            throw EntityValidationException(violations)
        }
    }

    private fun validateTwoDimensionalInputs(
        items: List<CargoItem>,
        weightCapacityKg: Int,
        volumeCapacityM3: Double
    ) {
        validateWeightInputs(items, weightCapacityKg)

        val violations = buildList {
            if (!volumeCapacityM3.isFinite()) {
                add(ValidatorError.NonFiniteNumber(ValidatorField.ENTITY))
            } else if (volumeCapacityM3 < 0.0) {
                add(ValidatorError.NegativeValue(ValidatorField.ENTITY))
            }

            if (items.any { !it.volumeM3.isFinite() }) {
                add(ValidatorError.NonFiniteNumber(ValidatorField.ENTITY))
            } else if (items.any { it.volumeM3 < 0.0 }) {
                add(ValidatorError.NegativeValue(ValidatorField.ENTITY))
            }
        }

        if (violations.isNotEmpty()) {
            throw EntityValidationException(violations)
        }
    }

    private fun buildValueGrid(
        items: List<CargoItem>,
        capacityKg: Int
    ): Array<IntArray> {
        val grid = Array(items.size + 1) { IntArray(capacityKg + 1) }

        for (i in 1..items.size) {
            val item = items[i - 1]
            for (weight in 0..capacityKg) {
                grid[i][weight] = if (item.weightKg <= weight) {
                    maxOf(
                        grid[i - 1][weight],
                        grid[i - 1][weight - item.weightKg] + item.priorityValue
                    )
                } else {
                    grid[i - 1][weight]
                }
            }
        }

        return grid
    }

    private fun backtrackSelectedItems(
        items: List<CargoItem>,
        grid: Array<IntArray>,
        capacityKg: Int
    ): List<CargoItem> {
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

    private fun buildTwoDimensionalValueGrid(
        items: List<CargoItem>,
        weightCapacityKg: Int,
        volumeCapacityUnits: Int
    ): Array<Array<IntArray>> {
        val grid = Array(items.size + 1) {
            Array(weightCapacityKg + 1) {
                IntArray(volumeCapacityUnits + 1)
            }
        }

        for (i in 1..items.size) {
            val item = items[i - 1]
            val itemVolumeUnits = toVolumeUnits(item.volumeM3)

            for (weight in 0..weightCapacityKg) {
                for (volume in 0..volumeCapacityUnits) {
                    grid[i][weight][volume] = calculateBestValue(
                        grid = grid,
                        item = item,
                        itemIndex = i,
                        weight = weight,
                        volume = volume,
                        itemVolumeUnits = itemVolumeUnits
                    )
                }
            }
        }

        return grid
    }

    private fun calculateBestValue(
        grid: Array<Array<IntArray>>,
        item: CargoItem,
        itemIndex: Int,
        weight: Int,
        volume: Int,
        itemVolumeUnits: Int
    ): Int {
        val withoutItem = grid[itemIndex - 1][weight][volume]
        val canFitItem = item.weightKg <= weight && itemVolumeUnits <= volume

        if (!canFitItem) return withoutItem

        val withItem = grid[itemIndex - 1][weight - item.weightKg][volume - itemVolumeUnits] + item.priorityValue
        return maxOf(withoutItem, withItem)
    }

    private fun backtrackTwoDimensionalItems(
        items: List<CargoItem>,
        grid: Array<Array<IntArray>>,
        weightCapacityKg: Int,
        volumeCapacityUnits: Int
    ): List<CargoItem> {
        val selected = mutableListOf<CargoItem>()
        var remainingWeight = weightCapacityKg
        var remainingVolume = volumeCapacityUnits

        for (i in items.size downTo 1) {
            val currentValue = grid[i][remainingWeight][remainingVolume]
            val previousValue = grid[i - 1][remainingWeight][remainingVolume]

            if (currentValue != previousValue) {
                val item = items[i - 1]
                val itemVolumeUnits = toVolumeUnits(item.volumeM3)

                selected.add(item)
                remainingWeight -= item.weightKg
                remainingVolume -= itemVolumeUnits
            }
        }

        return selected.reversed()
    }

    private fun toVolumeUnits(volumeM3: Double): Int =
        BigDecimal.valueOf(volumeM3)
            .multiply(BigDecimal(VOLUME_SCALE))
            .setScale(0, RoundingMode.HALF_UP)
            .intValueExact()
}