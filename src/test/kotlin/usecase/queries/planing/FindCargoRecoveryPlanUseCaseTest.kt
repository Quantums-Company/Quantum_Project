package usecase.queries.planing

import org.junit.jupiter.api.Test
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.queries.planing.FindCargoRecoveryPlanUseCase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FindCargoRecoveryPlanUseCaseTest {

    private val useCase = FindCargoRecoveryPlanUseCase()

    private fun warehouseWith(id: String = "WH-001") =
        Warehouse(
            id = id,
            name = "Hub",
            regionalZone = "Central",
            longitude = 34.0,
            latitude = 31.0
        )

    private fun vehicleWith(id: String, warehouse: Warehouse) =
        Vehicle(
            id = id,
            maxCapacityKg = 500.0,
            costPerKm = 2.0,
            currentWarehouse = warehouse
        )

    private fun packageWith(
        id: String,
        origin: Warehouse,
        destination: Warehouse,
        weight: Double = 10.0
    ) = Package(
        id = id,
        weight = weight,
        priority = Priority.STANDARD,
        originWarehouse = origin,
        destinationWarehouse = destination
    )

    @Test
    fun `when the warehouse has no packages should return null without building a ring`() {
        // Given
        val warehouse = warehouseWith()
        val failedVehicle = vehicleWith("TRK-001", warehouse)
        val healthyVehicle = vehicleWith("TRK-002", warehouse)

        warehouse.addVehicle(failedVehicle)
        warehouse.addVehicle(healthyVehicle)

        // When
        val result = useCase(failedVehicle)

        // Then
        assertNull(result)
    }

    @Test
    fun `when there is no other vehicle to recover to should return null`() {
        // Given
        val warehouse = warehouseWith()
        val destination = warehouseWith("WH-002")
        val failedVehicle = vehicleWith("TRK-001", warehouse)

        warehouse.addVehicle(failedVehicle)
        warehouse.addPackage(packageWith("PKG-001", warehouse, destination))

        // When
        val result = useCase(failedVehicle)

        // Then
        assertNull(result)
    }

    @Test
    fun `when recovery is possible, reroutes every affected package to a healthy vehicle, never back to the failed one`() {
        // Given
        val warehouse = warehouseWith()
        val destination = warehouseWith("WH-002")
        val failedVehicle = vehicleWith("TRK-001", warehouse)
        val healthyVehicle = vehicleWith("TRK-002", warehouse)

        warehouse.addVehicle(failedVehicle)
        warehouse.addVehicle(healthyVehicle)

        repeat(5) { index ->
            warehouse.addPackage(packageWith("PKG-00$index", warehouse, destination))
        }

        // When
        val result = useCase(failedVehicle)

        // Then
        assertNotNull(result, "Expected a recovery plan, got null")
        assertEquals(failedVehicle.id, result.failedVehicleId)
        assertTrue(result.rescueVehicleByPackageId.values.all { it == healthyVehicle.id })
    }

    @Test
    fun `with multiple healthy vehicles, no rerouted package ever lands back on the failed vehicle`() {
        // Given
        val warehouse = warehouseWith()
        val destination = warehouseWith("WH-002")
        val failedVehicle = vehicleWith("TRK-001", warehouse)
        val healthyOne = vehicleWith("TRK-002", warehouse)
        val healthyTwo = vehicleWith("TRK-003", warehouse)

        listOf(failedVehicle, healthyOne, healthyTwo).forEach(warehouse::addVehicle)

        repeat(8) { index ->
            warehouse.addPackage(packageWith("PKG-10$index", warehouse, destination, weight = 5.0))
        }

        // When
        val result = useCase(failedVehicle)

        // Then
        assertNotNull(result) //
        assertTrue(result.rescueVehicleByPackageId.values.none { it == failedVehicle.id })
        assertTrue(result.rescueVehicleByPackageId.values.all { it == healthyOne.id || it == healthyTwo.id })
    }
}
