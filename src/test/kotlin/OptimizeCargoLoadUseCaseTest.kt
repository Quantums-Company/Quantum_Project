import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.knapsack.KnapsackCargoOptimizer
import org.bytebloom.domain.model.CargoItem
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.usecase.queries.knapsack.OptimizeCargoLoadUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OptimizeCargoLoadUseCaseTest {

    private val optimizer = KnapsackCargoOptimizer()
    private val useCase = OptimizeCargoLoadUseCase(optimizer)

    private fun vehicleWith(capacityKg: Double): Vehicle {
        val vehicle = mockk<Vehicle>()
        every { vehicle.maxCapacityKg } returns capacityKg
        return vehicle
    }

    private fun packageWith(id: String, weightKg: Double, priorityScore: Int): Package {
        val pkg = mockk<Package>()
        every { pkg.id } returns id
        every { pkg.weight } returns weightKg
        every { pkg.priority.score } returns priorityScore
        return pkg
    }

    @Test
    fun `no items selects nothing and has zero weight and value`() {
        // Given
        val vehicle = vehicleWith(capacityKg = 10.0)

        // When
        val result = useCase(vehicle, candidatePackages = emptyList())

        // Then
        assertTrue(result.selectedItems.isEmpty())
        assertEquals(0, result.totalWeightKg)
        assertEquals(0, result.totalPriorityValue)
    }

    @Test
    fun `zero capacity selects nothing even when items are available`() {
        // Given
        val vehicle = vehicleWith(capacityKg = 0.0)
        val candidatePackages = listOf(
            packageWith(id = "PKG-1", weightKg = 5.0, priorityScore = 10)
        )

        // When
        val result = useCase(vehicle, candidatePackages)

        // Then
        assertTrue(result.selectedItems.isEmpty())
    }

    @Test
    fun `a single item that fits is selected`() {
        // Given
        val vehicle = vehicleWith(capacityKg = 5.0)
        val pkg = packageWith(id = "PKG-1", weightKg = 5.0, priorityScore = 10)
        val expectedCargoItem = CargoItem(id = "PKG-1", weightKg = 5, priorityValue = 10)

        // When
        val result = useCase(vehicle, listOf(pkg))

        // Then
        assertEquals(listOf(expectedCargoItem), result.selectedItems)
        assertEquals(5, result.totalWeightKg)
        assertEquals(10, result.totalPriorityValue)
    }

    @Test
    fun `a single item that exceeds capacity is rejected`() {
        // Given
        val vehicle = vehicleWith(capacityKg = 5.0)
        val pkg = packageWith(id = "PKG-1", weightKg = 20.0, priorityScore = 100)

        // When
        val result = useCase(vehicle, listOf(pkg))

        // Then
        assertTrue(result.selectedItems.isEmpty())
        assertEquals(0, result.totalPriorityValue)
    }

    @Test
    fun `when everything fits, selects every item`() {
        // Given
        val vehicle = vehicleWith(capacityKg = 10.0)
        val pkg1 = packageWith(id = "PKG-1", weightKg = 2.0, priorityScore = 3)
        val pkg2 = packageWith(id = "PKG-2", weightKg = 3.0, priorityScore = 4)

        val item1 = CargoItem(id = "PKG-1", weightKg = 2, priorityValue = 3)
        val item2 = CargoItem(id = "PKG-2", weightKg = 3, priorityValue = 4)

        // When
        val result = useCase(vehicle, listOf(pkg1, pkg2))

        // Then
        assertEquals(setOf(item1, item2), result.selectedItems.toSet())
        assertEquals(5, result.totalWeightKg)
        assertEquals(7, result.totalPriorityValue)
    }

    @Test
    fun `classic textbook case picks the optimal combination, not the greedy one`() {
        // Given — capacity 50; weights [10,20,30], values [60,100,120].
        // Greedy-by-value-per-weight would pick item1+item2 (value 160, weight 30, leaving 20kg unused);
        // the true DP optimum is item2+item3 (value 220, weight 50, full capacity used).
        val vehicle = vehicleWith(capacityKg = 50.0)
        val pkg1 = packageWith(id = "PKG-1", weightKg = 10.0, priorityScore = 60)
        val pkg2 = packageWith(id = "PKG-2", weightKg = 20.0, priorityScore = 100)
        val pkg3 = packageWith(id = "PKG-3", weightKg = 30.0, priorityScore = 120)

        val expectedItem2 = CargoItem(id = "PKG-2", weightKg = 20, priorityValue = 100)
        val expectedItem3 = CargoItem(id = "PKG-3", weightKg = 30, priorityValue = 120)

        // When
        val result = useCase(vehicle, listOf(pkg1, pkg2, pkg3))

        // Then
        assertEquals(220, result.totalPriorityValue)
        assertEquals(50, result.totalWeightKg)
        assertEquals(setOf(expectedItem2, expectedItem3), result.selectedItems.toSet())
    }

    @Test
    fun `an item with zero weight is always included if it has any positive priority`() {
        // Given
        val vehicle = vehicleWith(capacityKg = 10.0)
        val freePkg = packageWith(id = "PKG-FREE", weightKg = 0.0, priorityScore = 5)
        val normalPkg = packageWith(id = "PKG-1", weightKg = 10.0, priorityScore = 10)

        val expectedFreeItem = CargoItem(id = "PKG-FREE", weightKg = 0, priorityValue = 5)

        // When
        val result = useCase(vehicle, listOf(freePkg, normalPkg))

        // Then
        assertTrue(expectedFreeItem in result.selectedItems)
        assertEquals(15, result.totalPriorityValue)
    }

    @Test
    fun `duplicate priority values with different weights still picks the capacity-optimal combination`() {
        // Given — two items worth 10 each, only one fits alongside a third item
        val vehicle = vehicleWith(capacityKg = 8.0)
        val cheapPkg = packageWith(id = "PKG-CHEAP", weightKg = 3.0, priorityScore = 10)
        val expensivePkg = packageWith(id = "PKG-EXPENSIVE", weightKg = 8.0, priorityScore = 10)
        val fillerPkg = packageWith(id = "PKG-FILLER", weightKg = 5.0, priorityScore = 8)

        val cheapItem = CargoItem(id = "PKG-CHEAP", weightKg = 3, priorityValue = 10)
        val fillerItem = CargoItem(id = "PKG-FILLER", weightKg = 5, priorityValue = 8)

        // When — capacity 8: cheap(3)+filler(5)=8kg/18 value fits; expensive(8) alone = 10 value
        val result = useCase(vehicle, listOf(cheapPkg, expensivePkg, fillerPkg))

        // Then
        assertEquals(18, result.totalPriorityValue)
        assertEquals(setOf(cheapItem, fillerItem), result.selectedItems.toSet())
    }

    @Test
    fun `negative capacity is rejected with a typed domain exception`() {
        // Given
        val vehicle = vehicleWith(capacityKg = -1.0)
        val pkg = packageWith(id = "PKG-1", weightKg = 5.0, priorityScore = 10)

        // Then
        assertFailsWith<EntityValidationException> {
            useCase(vehicle, listOf(pkg))
        }
    }

    @Test
    fun `an item with negative weight is rejected with a typed domain exception`() {
        // Given
        val vehicle = vehicleWith(capacityKg = 10.0)
        val badPkg = packageWith(id = "PKG-BAD", weightKg = -5.0, priorityScore = 10)

        // Then
        assertFailsWith<EntityValidationException> {
            useCase(vehicle, listOf(badPkg))
        }
    }
}