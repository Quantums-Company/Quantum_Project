package org.bytebloom.domain.usecase.dispatch

import org.bytebloom.domain.model.Vehicle

data class DispatchResult(
    val selectedVehicles: List<Vehicle>,
    val coveredZones: Set<String>,
    val uncoveredZones: Set<String>
) {
    val isFullyCovered: Boolean
        get() = uncoveredZones.isEmpty()
}


private data class DispatchState(
    val uncoveredZones: Set<String>,
    val coveredZones: Set<String>,
    val selectedVehicles: List<Vehicle>,
    val candidateVehicles: List<Vehicle>
)

private data class VehicleCoverageGain(
    val vehicle: Vehicle,
    val newZones: Set<String>
)

/**
 * Greedy solver for the set-covering problem: given a set of target zones and a
 * fleet of vehicles (each covering some subset of zones), repeatedly pick the
 * vehicle that covers the most *currently uncovered* zones until every zone is
 * covered or no candidate can help anymore.
 *
 * Complexity: O(N^2) vs brute force O(2^N)
 * -----------------------------------------
 * The exact set-covering problem is NP-hard: a brute-force solution would need to
 * examine every possible subset of the N available vehicles (2^N combinations) to
 * find the minimum-size cover — infeasible once N grows past ~20-30 vehicles.
 *
 * This greedy heuristic instead runs at most N selection rounds (one vehicle
 * picked per round, in the worst case), and each round scans the remaining
 * candidates (at most N) to find the best one — O(N) work per round, O(N) rounds,
 * so O(N^2) total. It doesn't guarantee the mathematically minimum-size fleet
 * (that trade-off is the well-known approximation gap of the greedy set-cover
 * heuristic, discussed in Grokking Algorithms Ch. 10), but it produces a
 * provably good cover (within a ln(N) factor of optimal) in polynomial time,
 * which is what makes it usable at dispatch-time on live data instead of only
 * offline on small inputs.
 */
class GreedyFleetDispatchUseCase {

    operator fun invoke(
        targetZones: Set<String>,
        availableVehicles: List<Vehicle>,
        coverageOf: (Vehicle) -> Set<String>
    ): DispatchResult {

        val initialState = DispatchState(
            uncoveredZones = targetZones,
            coveredZones = emptySet(),
            selectedVehicles = emptyList(),
            candidateVehicles = availableVehicles
        )

        val finalState = dispatchStep(
            initialState,
            coverageOf
        )

        return DispatchResult(
            selectedVehicles = finalState.selectedVehicles,
            coveredZones = finalState.coveredZones,
            uncoveredZones = finalState.uncoveredZones
        )
    }

    private tailrec fun dispatchStep(
        state: DispatchState,
        coverageOf: (Vehicle) -> Set<String>
    ): DispatchState {

        if (
            state.uncoveredZones.isEmpty() ||
            state.candidateVehicles.isEmpty()
        ) { return state }

        val bestCandidate = findBestCandidate(
            candidates = state.candidateVehicles,
            uncoveredZones = state.uncoveredZones,
            coverageOf = coverageOf
        ) ?: return state

        return dispatchStep(
            applySelection(state, bestCandidate),
            coverageOf
        )
    }

    private fun findBestCandidate(
        candidates: List<Vehicle>,
        uncoveredZones: Set<String>,
        coverageOf: (Vehicle) -> Set<String>
    ): VehicleCoverageGain? {

        return candidates
            .map { vehicle -> VehicleCoverageGain(
                    vehicle = vehicle,
                    newZones = coverageOf(vehicle)
                        .intersect(uncoveredZones)) }
            .filter { candidate -> candidate.newZones.isNotEmpty() }
            .maxByOrNull { candidate -> candidate.newZones.size }
    }

    private fun applySelection(
        state: DispatchState,
        candidate: VehicleCoverageGain
    ): DispatchState {

        return state.copy(
            uncoveredZones = state.uncoveredZones - candidate.newZones,
            coveredZones = state.coveredZones + candidate.newZones,
            selectedVehicles = state.selectedVehicles + candidate.vehicle,
            candidateVehicles = state.candidateVehicles - candidate.vehicle
        )
    }
}
