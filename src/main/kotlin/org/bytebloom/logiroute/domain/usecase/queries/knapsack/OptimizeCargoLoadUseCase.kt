package org.bytebloom.logiroute.domain.usecase.queries.knapsack

import org.bytebloom.logiroute.domain.knapsack.KnapsackCargoOptimizer
import org.bytebloom.logiroute.domain.model.CargoItem
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.result.KnapsackResult
import kotlin.math.roundToInt

class OptimizeCargoLoadUseCase(
    private val optimizer: KnapsackCargoOptimizer
) {

    operator fun invoke(
        vehicle: Vehicle,
        candidatePackages: List<Package>
    ): KnapsackResult {
        val items = candidatePackages.map { pkg ->
            CargoItem(
                id = pkg.id,
                weightKg = pkg.weight.roundToInt(),
                priorityValue = pkg.priority.score
            )
        }

        return optimizer(
            items = items,
            capacityKg = vehicle.maxCapacityKg.roundToInt()
        )
    }

    fun optimizeWithVolume(
        vehicle: Vehicle,
        candidatePackages: List<Package>,
        volumeByPackageId: Map<String, Double>,
        maxVolumeM3: Double
    ): KnapsackResult {
        val items = candidatePackages.map { pkg ->
            CargoItem(
                id = pkg.id,
                weightKg = pkg.weight.roundToInt(),
                priorityValue = pkg.priority.score,
                volumeM3 = volumeByPackageId.getValue(pkg.id)
            )
        }

        return optimizer(
            items = items,
            weightCapacityKg = vehicle.maxCapacityKg.roundToInt(),
            volumeCapacityM3 = maxVolumeM3
        )
    }
}