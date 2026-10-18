package org.bytebloom.logiroute.domain.usecase.dispatch

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FindCargoRecoveryPlanUseCaseTest {

    private lateinit var findCargoRecoveryPlan: FindCargoRecoveryPlanUseCase
    private lateinit var warehouse: Warehouse
    private lateinit var failedVehicle: Vehicle

    @BeforeEach
    fun setUp() {
        findCargoRecoveryPlan = FindCargoRecoveryPlanUseCase()
        warehouse = createWarehouse("WH-001")
        failedVehicle = createVehicle("TRK-001")
        warehouse.addVehicle(failedVehicle)
    }

    @Test
    fun `should return null when warehouse has no packages`() {
        // Given
        warehouse.addVehicle(createVehicle("TRK-002"))

        // When
        val plan = findCargoRecoveryPlan(failedVehicle)

        // Then
        assertThat(plan).isNull()
    }

    @Test
    fun `should return null when no healthy vehicle is available`() {
        // Given
        queuePackages(count = 1)

        // When
        val plan = findCargoRecoveryPlan(failedVehicle)

        // Then
        assertThat(plan).isNull()
    }

    @Test
    fun `should assign affected packages to the only healthy vehicle when one is available`() {
        // Given
        val healthyVehicle = createVehicle("TRK-002")
        warehouse.addVehicle(healthyVehicle)
        queuePackages(count = 5)

        // When
        val plan = findCargoRecoveryPlan(failedVehicle)

        // Then
        assertThat(plan).isNotNull()
        assertThat(plan!!.failedVehicleId).isEqualTo(failedVehicle.id)
        assertThat(listOf(healthyVehicle.id)).containsAtLeastElementsIn(plan.rescueVehicleByPackageId.values)
    }

    @Test
    fun `should never assign packages back to failed vehicle when several healthy vehicles exist`() {
        // Given
        val healthyOne = createVehicle("TRK-002")
        val healthyTwo = createVehicle("TRK-003")
        warehouse.addVehicle(healthyOne)
        warehouse.addVehicle(healthyTwo)
        queuePackages(count = 8)

        // When
        val plan = findCargoRecoveryPlan(failedVehicle)

        // Then
        assertThat(plan).isNotNull()
        assertThat(plan!!.rescueVehicleByPackageId.values).doesNotContain(failedVehicle.id)
        assertThat(listOf(healthyOne.id, healthyTwo.id))
            .containsAtLeastElementsIn(plan.rescueVehicleByPackageId.values)
    }

    private fun queuePackages(count: Int) {
        repeat(count) { index ->
            warehouse.addPackage(createPackage(id = "PKG-" + (index + 1).toString().padStart(3, '0')))
        }
    }

    private fun createVehicle(id: String) = Vehicle(
        id = id,
        maxCapacityKg = 500.0,
        costPerKm = 2.0,
        currentWarehouse = warehouse
    )

    private fun createPackage(id: String) = Package(
        id = id,
        weight = 10.0,
        priority = Priority.STANDARD,
        originWarehouse = warehouse,
        destinationWarehouse = DESTINATION
    )

    private companion object {
        val DESTINATION = createWarehouse("WH-002")

        fun createWarehouse(id: String) = Warehouse(
            id = id,
            name = "Hub $id",
            regionalZone = "CENTRAL",
            longitude = 34.0,
            latitude = 31.0
        )
    }
}