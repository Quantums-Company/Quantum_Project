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

        val priorityValueTable = buildValueGrid(items = items, capacityKg = capacityKg)
        val selectedItems = backtrackSelectedItems(
            items = items,
            priorityValueTable = priorityValueTable,
            capacityKg = capacityKg
        )

        return KnapsackResult(
            selectedItems = selectedItems,
            totalWeightKg = selectedItems.sumOf { it.weightKg },
            totalPriorityValue = priorityValueTable[items.size][capacityKg],
            totalVolumeM3 = selectedItems.sumOf { it.volumeM3 }
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

        val priorityValueTable = buildTwoDimensionalValueGrid(
            items = items,
            weightCapacityKg = weightCapacityKg,
            volumeCapacityUnits = volumeCapacityUnits
        )

        val selectedItems = backtrackTwoDimensionalItems(
            items = items,
            priorityValueTable = priorityValueTable,
            weightCapacityKg = weightCapacityKg,
            volumeCapacityUnits = volumeCapacityUnits
        )

        return KnapsackResult(
            selectedItems = selectedItems,
            totalWeightKg = selectedItems.sumOf { it.weightKg },
            totalPriorityValue = selectedItems.sumOf { it.priorityValue },
            totalVolumeM3 = selectedItems.sumOf { it.volumeM3 }
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
        val priorityValueTable = Array(items.size + 1) { IntArray(capacityKg + 1) }

        for (i in 1..items.size) {
            val item = items[i - 1]
            for (weight in 0..capacityKg) {
                priorityValueTable[i][weight] = if (item.weightKg <= weight) {
                    maxOf(
                        priorityValueTable[i - 1][weight],
                        priorityValueTable[i - 1][weight - item.weightKg] + item.priorityValue
                    )
                } else {
                    priorityValueTable[i - 1][weight]
                }
            }
        }

        return priorityValueTable
    }

    private fun backtrackSelectedItems(
        items: List<CargoItem>,
        priorityValueTable: Array<IntArray>,
        capacityKg: Int
    ): List<CargoItem> {
        val selectedItems = mutableListOf<CargoItem>()
        var remainingWeightCapacityKg = capacityKg

        for (i in items.size downTo 1) {
            if (priorityValueTable[i][remainingWeightCapacityKg] !=
                priorityValueTable[i - 1][remainingWeightCapacityKg]) {
                val item = items[i - 1]
                selectedItems.add(item)
                remainingWeightCapacityKg -= item.weightKg
            }
        }

        return selectedItems.reversed()
    }

    private fun buildTwoDimensionalValueGrid(
        items: List<CargoItem>,
        weightCapacityKg: Int,
        volumeCapacityUnits: Int
    ): Array<Array<IntArray>> {
        val priorityValueTable = Array(items.size + 1) {
            Array(weightCapacityKg + 1) {
                IntArray(volumeCapacityUnits + 1)
            }
        }

        for (i in 1..items.size) {
            val item = items[i - 1]
            val cargoVolumeUnits = toVolumeUnits(item.volumeM3)

            for (weight in 0..weightCapacityKg) {
                for (volume in 0..volumeCapacityUnits) {
                    priorityValueTable[i][weight][volume] = calculateBestValue(
                        priorityValueTable = priorityValueTable,
                        item = item,
                        itemIndex = i,
                        weight = weight,
                        volume = volume,
                        cargoVolumeUnits = cargoVolumeUnits
                    )
                }
            }
        }

        return priorityValueTable
    }

    private fun calculateBestValue(
        priorityValueTable: Array<Array<IntArray>>,
        item: CargoItem,
        itemIndex: Int,
        weight: Int,
        volume: Int,
        cargoVolumeUnits: Int
    ): Int {
        val valueWithoutItem = priorityValueTable[itemIndex - 1][weight][volume]
        val itemFitsCapacity = item.weightKg <= weight && cargoVolumeUnits <= volume

        if (!itemFitsCapacity) return valueWithoutItem

        val valueWithItem =
            priorityValueTable[itemIndex - 1][weight - item.weightKg][volume - cargoVolumeUnits] +
                    item.priorityValue
        return maxOf(valueWithoutItem, valueWithItem)
    }

    private fun backtrackTwoDimensionalItems(
        items: List<CargoItem>,
        priorityValueTable: Array<Array<IntArray>>,
        weightCapacityKg: Int,
        volumeCapacityUnits: Int
    ): List<CargoItem> {
        val selectedItems = mutableListOf<CargoItem>()
        var remainingWeightCapacityKg = weightCapacityKg
        var remainingVolumeCapacityUnits = volumeCapacityUnits

        for (i in items.size downTo 1) {
            val currentPriorityValue =
                priorityValueTable[i][remainingWeightCapacityKg][remainingVolumeCapacityUnits]
            val previousPriorityValue =
                priorityValueTable[i - 1][remainingWeightCapacityKg][remainingVolumeCapacityUnits]

            if (currentPriorityValue != previousPriorityValue) {
                val item = items[i - 1]
                val cargoVolumeUnits = toVolumeUnits(item.volumeM3)

                selectedItems.add(item)
                remainingWeightCapacityKg -= item.weightKg
                remainingVolumeCapacityUnits -= cargoVolumeUnits
            }
        }

        return selectedItems.reversed()
    }

    private fun toVolumeUnits(volumeM3: Double): Int =
        BigDecimal.valueOf(volumeM3)
            .multiply(BigDecimal(VOLUME_SCALE))
            .setScale(0, RoundingMode.HALF_UP)
            .intValueExact()
}