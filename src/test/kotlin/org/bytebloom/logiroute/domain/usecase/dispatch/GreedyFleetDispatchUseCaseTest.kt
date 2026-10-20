package org.bytebloom.logiroute.domain.usecase.dispatch

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GreedyFleetDispatchUseCaseTest {

    private lateinit var greedyFleetDispatch: GreedyFleetDispatchUseCase
    private lateinit var northCentralVehicle: Vehicle
    private lateinit var centralSouthVehicle: Vehicle
    private lateinit var southEastVehicle: Vehicle
    private lateinit var fleet: List<Vehicle>
    private lateinit var coverageOf: (Vehicle) -> Set<String>

    @BeforeEach
    fun setUp() {
        greedyFleetDispatch = GreedyFleetDispatchUseCase()
        northCentralVehicle = createVehicle("TRK-001")
        centralSouthVehicle = createVehicle("TRK-002")
        southEastVehicle = createVehicle("TRK-003")
        fleet = listOf(northCentralVehicle, centralSouthVehicle, southEastVehicle)

        val coverage = mapOf(
            northCentralVehicle to setOf(NORTH, CENTRAL),
            centralSouthVehicle to setOf(CENTRAL, SOUTH),
            southEastVehicle to setOf(SOUTH, EAST)
        )
        coverageOf = { vehicle -> coverage[vehicle].orEmpty() }
    }

    @Test
    fun `should select fewest vehicles when they can fully cover all target zones`() {
        // Given
        val targetZones = setOf(NORTH, CENTRAL, SOUTH, EAST)

        // When
        val result = greedyFleetDispatch(targetZones, fleet, coverageOf)

        // Then
        assertThat(result.isFullyCovered).isTrue()
        assertThat(result.coveredZones).isEqualTo(targetZones)
        assertThat(result.selectedVehicles).containsExactly(northCentralVehicle, southEastVehicle)
    }

    @Test
    fun `should skip vehicle when it adds no new zones`() {
        // Given
        val targetZones = setOf(NORTH, CENTRAL, SOUTH, EAST)

        // When
        val result = greedyFleetDispatch(targetZones, fleet, coverageOf)

        // Then
        assertThat(result.selectedVehicles).doesNotContain(centralSouthVehicle)
    }

    @Test
    fun `should report uncovered zones when no vehicle can reach them`() {
        // Given
        val targetZones = setOf(NORTH, CENTRAL, WEST)

        // When
        val result = greedyFleetDispatch(targetZones, fleet, coverageOf)

        // Then
        assertThat(result.isFullyCovered).isFalse()
        assertThat(result.uncoveredZones).containsExactly(WEST)
        assertThat(result.coveredZones).containsExactly(NORTH, CENTRAL)
    }

    @Test
    fun `should select no vehicles when target zones are empty`() {
        // Given
        val targetZones = emptySet<String>()

        // When
        val result = greedyFleetDispatch(targetZones, fleet, coverageOf)

        // Then
        assertThat(result.isFullyCovered).isTrue()
        assertThat(result.selectedVehicles).isEmpty()
    }

    @Test
    fun `should leave every zone uncovered when no vehicles are available`() {
        // Given
        val targetZones = setOf(NORTH, SOUTH)

        // When
        val result = greedyFleetDispatch(targetZones, emptyList(), coverageOf)

        // Then
        assertThat(result.isFullyCovered).isFalse()
        assertThat(result.uncoveredZones).isEqualTo(targetZones)
        assertThat(result.selectedVehicles).isEmpty()
    }

    @Test
    fun `should pick first vehicle in list order when vehicles tie on coverage`() {
        // Given
        val tiedCoverage: (Vehicle) -> Set<String> = { setOf(NORTH) }
        val tiedFleet = listOf(northCentralVehicle, centralSouthVehicle)

        // When
        val result = greedyFleetDispatch(setOf(NORTH), tiedFleet, tiedCoverage)

        // Then
        assertThat(result.selectedVehicles).containsExactly(northCentralVehicle)
    }

    private fun createVehicle(id: String) = Vehicle(
        id = id,
        maxCapacityKg = 500.0,
        costPerKm = 2.0,
        currentWarehouse = HOME_WAREHOUSE
    )

    private companion object {
        const val NORTH = "NORTH"
        const val CENTRAL = "CENTRAL"
        const val SOUTH = "SOUTH"
        const val EAST = "EAST"
        const val WEST = "WEST"

        val HOME_WAREHOUSE = Warehouse(
            id = "WH-001",
            name = "Hub",
            regionalZone = "CENTRAL",
            longitude = 34.0,
            latitude = 31.0
        )
    }
}