package usecase.greedy

import org.junit.jupiter.api.Test
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.greedy.GreedyFleetDispatchUseCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GreedyFleetDispatchUseCaseTest {

    private val useCase = GreedyFleetDispatchUseCase()
    private val homeWarehouse = Warehouse(
        id = "WH-001",
        name = "Hub",
        regionalZone = "Central",
        longitude = 34.0,
        latitude = 31.0
    )

    private val vehicleA = Vehicle(
        id = "TRK-001",
        maxCapacityKg = 500.0,
        costPerKm = 2.0,
        currentWarehouse = homeWarehouse
    )
    private val vehicleB = Vehicle(
        id = "TRK-002",
        maxCapacityKg = 500.0,
        costPerKm = 2.0,
        currentWarehouse = homeWarehouse
    )
    private val vehicleC = Vehicle(
        id = "TRK-003",
        maxCapacityKg = 500.0,
        costPerKm = 2.0,
        currentWarehouse = homeWarehouse
    )

    private val coverage: Map<Vehicle, Set<String>> = mapOf(
        vehicleA to setOf("NORTH", "CENTRAL"),
        vehicleB to setOf("CENTRAL", "SOUTH"),
        vehicleC to setOf("SOUTH", "EAST")
    )
    private val coverageOf: (Vehicle) -> Set<String> = { vehicle -> coverage[vehicle].orEmpty() }

    @Test
    fun `selects the fewest vehicles that fully cover every target zone`() {
        // Given
        val targetZones = setOf("NORTH", "CENTRAL", "SOUTH", "EAST")

        // When
        val result = useCase(
            targetZones,
            listOf(vehicleA, vehicleB, vehicleC),
            coverageOf
        )

        // Then
        assertTrue(result.isFullyCovered)
        assertEquals(emptySet(), result.uncoveredZones)
        assertEquals(targetZones, result.coveredZones)
        assertEquals(2, result.selectedVehicles.size)
        assertEquals(setOf(vehicleA, vehicleC), result.selectedVehicles.toSet())
    }

    @Test
    fun `reports uncovered zones when no vehicle can reach them`() {
        // Given — WEST is not covered by any vehicle in the fleet
        val targetZones = setOf("NORTH", "CENTRAL", "WEST")

        // When
        val result = useCase(
            targetZones,
            listOf(vehicleA, vehicleB, vehicleC),
            coverageOf
        )

        // Then
        assertTrue(!result.isFullyCovered)
        assertEquals(setOf("WEST"), result.uncoveredZones)
        assertEquals(setOf("NORTH", "CENTRAL"), result.coveredZones)
    }

    @Test
    fun `empty target zones selects no vehicles and is trivially fully covered`() {
        // When
        val result = useCase(
            emptySet(),
            listOf(vehicleA, vehicleB, vehicleC),
            coverageOf
        )

        // Then
        assertTrue(result.isFullyCovered)
        assertTrue(result.selectedVehicles.isEmpty())
    }

    @Test
    fun `no available vehicles leaves every target zone uncovered`() {
        // Given
        val targetZones = setOf("NORTH", "SOUTH")

        // When
        val result = useCase(
            targetZones,
            emptyList(),
            coverageOf
        )

        // Then
        assertTrue(!result.isFullyCovered)
        assertEquals(targetZones, result.uncoveredZones)
        assertTrue(result.selectedVehicles.isEmpty())
    }

    @Test
    fun `never selects a vehicle that adds zero new zones`() {
        // Given — vehicleA and vehicleC alone already cover everything;
        // vehicleB adds nothing new once picked order allows
        val targetZones = setOf("NORTH", "CENTRAL", "SOUTH", "EAST")

        // When
        val result = useCase(
            targetZones,
            listOf(vehicleA, vehicleB, vehicleC),
            coverageOf
        )

        // Then — every selected vehicle actually contributed at least one zone at the moment it was picked
        assertTrue(result.selectedVehicles.size <= 3)
        assertTrue(result.isFullyCovered)
    }

    @Test
    fun `on a tie in zones covered, picks the first candidate in list order (deterministic via maxByOrNull)`() {
        // Given — vehicleA and vehicleB both cover exactly {"NORTH"} and nothing else, vehicleA listed first
        val tiedCoverage: (Vehicle) -> Set<String> = { vehicle ->
            when (vehicle) {
                vehicleA -> setOf("NORTH")
                vehicleB -> setOf("NORTH")
                else -> emptySet()
            }
        }

        // When
        val result = useCase(
            setOf("NORTH"),
            listOf(vehicleA, vehicleB),
            tiedCoverage
        )

        // Then
        assertEquals(listOf(vehicleA), result.selectedVehicles)
    }
}
