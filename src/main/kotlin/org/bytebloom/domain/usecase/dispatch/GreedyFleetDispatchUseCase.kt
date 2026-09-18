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
        ) {
            return state
        }

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
