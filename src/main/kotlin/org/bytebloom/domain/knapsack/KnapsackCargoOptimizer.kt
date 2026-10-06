package org.bytebloom.domain.knapsack

import org.bytebloom.domain.model.CargoItem
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.result.KnapsackResult
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import java.math.BigDecimal
import java.math.RoundingMode

class KnapsackCargoOptimizer {

    companion object {
        private const val VOLUME_SCALE = 100
    }

    /**
     * Original 1D Knapsack.
     *
     * Optimizes cargo using weight capacity only.
     */
    operator fun invoke(
        items: List<CargoItem>,
        capacityKg: Int
    ): KnapsackResult {
        validateWeightInputs(items, capacityKg)

        val grid = buildValueGrid(
            items = items,
            capacityKg = capacityKg
        )

        val selected = backtrackSelectedItems(
            items = items,
            grid = grid,
            capacityKg = capacityKg
        )

        return KnapsackResult(
            selectedItems = selected,
            totalWeightKg = selected.sumOf { it.weightKg },
            totalPriorityValue = grid[items.size][capacityKg],
            totalVolumeM3 = selected.sumOf { it.volumeM3 }
        )
    }

    /**
     * Bonus 2D Knapsack.
     *
     * Optimizes cargo using both weight and volume constraints.
     */
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
        if (capacityKg < 0) {
            throw EntityValidationException(
                listOf(
                    ValidatorError.NegativeValue(
                        ValidatorField.MAX_CAPACITY_KG
                    )
                )
            )
        }

        if (items.any { it.weightKg < 0 }) {
            throw EntityValidationException(
                listOf(
                    ValidatorError.NegativeValue(
                        ValidatorField.WEIGHT
                    )
                )
            )
        }
    }

    private fun validateTwoDimensionalInputs(
        items: List<CargoItem>,
        weightCapacityKg: Int,
        volumeCapacityM3: Double
    ) {
        validateWeightInputs(
            items = items,
            capacityKg = weightCapacityKg
        )

        require(volumeCapacityM3 >= 0.0) {
            "Volume capacity cannot be negative."
        }

        require(items.none { it.volumeM3 < 0.0 }) {
            "Cargo volume cannot be negative."
        }
    }

    private fun buildValueGrid(
        items: List<CargoItem>,
        capacityKg: Int
    ): Array<IntArray> {
        val grid = Array(items.size + 1) {
            IntArray(capacityKg + 1)
        }

        for (i in 1..items.size) {
            val item = items[i - 1]

            for (weight in 0..capacityKg) {
                grid[i][weight] =
                    if (item.weightKg <= weight) {
                        maxOf(
                            grid[i - 1][weight],
                            grid[i - 1][weight - item.weightKg] +
                                    item.priorityValue
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
            if (
                grid[i][remainingCapacity] !=
                grid[i - 1][remainingCapacity]
            ) {
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
                    val withoutItem =
                        grid[i - 1][weight][volume]

                    val withItem =
                        if (
                            item.weightKg <= weight &&
                            itemVolumeUnits <= volume
                        ) {
                            grid[i - 1][weight - item.weightKg][volume - itemVolumeUnits] +
                                    item.priorityValue
                        } else {
                            withoutItem
                        }

                    grid[i][weight][volume] =
                        maxOf(withoutItem, withItem)
                }
            }
        }

        return grid
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
            val currentValue =
                grid[i][remainingWeight][remainingVolume]

            val previousValue =
                grid[i - 1][remainingWeight][remainingVolume]

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