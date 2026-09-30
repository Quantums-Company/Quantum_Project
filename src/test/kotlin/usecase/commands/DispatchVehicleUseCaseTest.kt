package usecase.commands

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.DispatchVehicleUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DispatchVehicleUseCaseTest {

    private lateinit var dispatchVehicle: DispatchVehicleUseCase
    private lateinit var warehouse: Warehouse
    private lateinit var vehicle: Vehicle

    @BeforeEach
    fun setUp() {
        dispatchVehicle = DispatchVehicleUseCase()
        warehouse = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        vehicle = createVehicle(maxCapacityKg = VEHICLE_CAPACITY_KG)
        warehouse.addVehicle(vehicle)
    }

    @Test
    fun `should return true when vehicle can carry all queued packages`() {
        // Given
        val packages = queuePackages(weights = listOf(30.0, 40.0))

        // When
        val isDispatched = dispatchVehicle(packages, vehicle, warehouse)

        // Then
        assertThat(isDispatched).isTrue()
    }

    @Test
    fun `should remove dispatched packages from cargo queue when dispatch succeeds`() {
        // Given
        val packages = queuePackages(weights = listOf(30.0, 40.0))

        // When
        dispatchVehicle(packages, vehicle, warehouse)

        // Then
        assertThat(warehouse.cargoQueue).isEmpty()
    }

    @Test
    fun `should remove vehicle from warehouse when dispatch succeeds`() {
        // Given
        val packages = queuePackages(weights = listOf(30.0))

        // When
        dispatchVehicle(packages, vehicle, warehouse)

        // Then
        assertThat(warehouse.hasVehicle(vehicle)).isFalse()
    }

    @Test
    fun `should keep packages that were not dispatched in cargo queue`() {
        // Given
        val dispatchedPackages = queuePackages(weights = listOf(30.0))
        val remainingPackages = queuePackages(weights = listOf(20.0), firstId = 2)

        // When
        dispatchVehicle(dispatchedPackages, vehicle, warehouse)

        // Then
        assertThat(warehouse.cargoQueue).containsExactlyElementsIn(remainingPackages)
    }

    @Test
    fun `should dispatch when total weight exactly equals vehicle capacity`() {
        // Given
        val packages = queuePackages(weights = listOf(60.0, 40.0))

        // When
        val isDispatched = dispatchVehicle(packages, vehicle, warehouse)

        // Then
        assertThat(isDispatched).isTrue()
    }

    @Test
    fun `should return false when total weight exceeds vehicle capacity`() {
        // Given
        val packages = queuePackages(weights = listOf(60.0, 50.0))

        // When
        val isDispatched = dispatchVehicle(packages, vehicle, warehouse)

        // Then
        assertThat(isDispatched).isFalse()
    }

    @Test
    fun `should keep warehouse unchanged when dispatch fails`() {
        // Given
        val packages = queuePackages(weights = listOf(60.0, 50.0))

        // When
        dispatchVehicle(packages, vehicle, warehouse)

        // Then
        assertThat(warehouse.cargoQueue).containsExactlyElementsIn(packages)
        assertThat(warehouse.hasVehicle(vehicle)).isTrue()
    }

    @Test
    fun `should return false when a package is not in warehouse cargo queue`() {
        // Given
        val unqueuedPackage = createPackage(id = "PKG-099", weight = 10.0)

        // When
        val isDispatched = dispatchVehicle(listOf(unqueuedPackage), vehicle, warehouse)

        // Then
        assertThat(isDispatched).isFalse()
    }

    @Test
    fun `should return false when vehicle is not stationed in warehouse`() {
        // Given
        val packages = queuePackages(weights = listOf(30.0))
        val unstationedVehicle = createVehicle(maxCapacityKg = VEHICLE_CAPACITY_KG)

        // When
        val isDispatched = dispatchVehicle(packages, unstationedVehicle, warehouse)

        // Then
        assertThat(isDispatched).isFalse()
    }

    @Test
    fun `should return false when there are no packages to dispatch`() {
        // Given
        val noPackages = emptyList<Package>()

        // When
        val isDispatched = dispatchVehicle(noPackages, vehicle, warehouse)

        // Then
        assertThat(isDispatched).isFalse()
    }

    private fun queuePackages(weights: List<Double>, firstId: Int = 1): List<Package> =
        weights.mapIndexed { index, weight ->
            createPackage(id = "PKG-%03d".format(firstId + index), weight = weight)
        }.onEach(warehouse::addPackage)

    private fun createPackage(id: String, weight: Double) = Package(
        id = id,
        weight = weight,
        priority = Priority.STANDARD,
        originWarehouse = warehouse,
        destinationWarehouse = DESTINATION
    )

    private fun createVehicle(maxCapacityKg: Double) = Vehicle(
        id = "TRK-001",
        maxCapacityKg = maxCapacityKg,
        costPerKm = 2.5,
        currentWarehouse = warehouse
    )

    private companion object {
        const val VEHICLE_CAPACITY_KG = 100.0
        val DESTINATION = createWarehouse(id = "WH-002", name = "Nablus Depot")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "West Bank",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}