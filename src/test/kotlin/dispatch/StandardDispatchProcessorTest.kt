package dispatch

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bytebloom.domain.dispatch.DispatchOrder
import org.bytebloom.domain.dispatch.DispatchOutcome
import org.bytebloom.domain.dispatch.DispatchRejectionReason
import org.bytebloom.domain.dispatch.ShipmentStateUpdater
import org.bytebloom.domain.dispatch.StandardDispatchProcessor
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StandardDispatchProcessorTest {

    private lateinit var shipmentStateUpdater: ShipmentStateUpdater
    private lateinit var standardProcessor: StandardDispatchProcessor
    private lateinit var warehouse: Warehouse
    private lateinit var vehicle: Vehicle

    @BeforeEach
    fun setUp() {
        shipmentStateUpdater = mockk(relaxUnitFun = true)
        standardProcessor = StandardDispatchProcessor(shipmentStateUpdater)
        warehouse = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        vehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = VEHICLE_CAPACITY_KG,
            costPerKm = 2.5,
            currentWarehouse = warehouse
        )
        warehouse.addVehicle(vehicle)
    }

    @Test
    fun `should dispatch packages of any priority within full vehicle capacity`() {
        // Given
        val order = createOrder(
            queuePackage(id = "PKG-001", weight = 300.0, priority = Priority.LOW),
            queuePackage(id = "PKG-002", weight = 400.0, priority = Priority.STANDARD)
        )

        // When
        val outcome = standardProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(DispatchOutcome.Dispatched(order))
    }

    @Test
    fun `should dispatch when total weight exactly equals full vehicle capacity`() {
        // Given
        val order = createOrder(queuePackage(id = "PKG-001", weight = VEHICLE_CAPACITY_KG))

        // When
        val outcome = standardProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(DispatchOutcome.Dispatched(order))
    }

    @Test
    fun `should remove dispatched packages and vehicle from warehouse when dispatched`() {
        // Given
        val order = createOrder(queuePackage(id = "PKG-001", weight = 300.0))

        // When
        standardProcessor.dispatch(order)

        // Then
        assertThat(warehouse.cargoQueue).isEmpty()
        assertThat(warehouse.hasVehicle(vehicle)).isFalse()
    }

    @Test
    fun `should update shipment state exactly once when dispatched`() {
        // Given
        val order = createOrder(queuePackage(id = "PKG-001", weight = 300.0))

        // When
        standardProcessor.dispatch(order)

        // Then
        verify(exactly = 1) { shipmentStateUpdater.markAsDispatched(order) }
    }

    @Test
    fun `should reserve vehicle capacity before updating shipment state`() {
        // Given
        val order = createOrder(queuePackage(id = "PKG-001", weight = 300.0))
        var wasQueueEmptyWhenStateUpdated = false
        every { shipmentStateUpdater.markAsDispatched(order) } answers {
            wasQueueEmptyWhenStateUpdated = warehouse.cargoQueue.isEmpty()
        }

        // When
        standardProcessor.dispatch(order)

        // Then
        assertThat(wasQueueEmptyWhenStateUpdated).isTrue()
    }

    @Test
    fun `should reject with capacity exceeded when total weight is above vehicle capacity`() {
        // Given
        val order = createOrder(
            queuePackage(id = "PKG-001", weight = 600.0),
            queuePackage(id = "PKG-002", weight = 500.0)
        )

        // When
        val outcome = standardProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(
            DispatchOutcome.Rejected(order, DispatchRejectionReason.CAPACITY_EXCEEDED)
        )
    }

    @Test
    fun `should reject with no packages when order is empty`() {
        // Given
        val order = createOrder()

        // When
        val outcome = standardProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(DispatchOutcome.Rejected(order, DispatchRejectionReason.NO_PACKAGES))
    }

    @Test
    fun `should reject with package not in queue when a package was never queued`() {
        // Given
        val unqueuedPackage = createPackage(id = "PKG-099", weight = 100.0)
        val order = createOrder(unqueuedPackage)

        // When
        val outcome = standardProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(
            DispatchOutcome.Rejected(order, DispatchRejectionReason.PACKAGE_NOT_IN_QUEUE)
        )
    }

    @Test
    fun `should reject with vehicle not stationed when vehicle is not in warehouse`() {
        // Given
        val order = createOrder(queuePackage(id = "PKG-001", weight = 300.0))
        warehouse.removeVehicle(vehicle)

        // When
        val outcome = standardProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(
            DispatchOutcome.Rejected(order, DispatchRejectionReason.VEHICLE_NOT_STATIONED)
        )
    }

    @Test
    fun `should keep warehouse unchanged and skip state update when rejected`() {
        // Given
        val heavyPackage = queuePackage(id = "PKG-001", weight = 1500.0)
        val order = createOrder(heavyPackage)

        // When
        standardProcessor.dispatch(order)

        // Then
        assertThat(warehouse.cargoQueue).containsExactly(heavyPackage)
        assertThat(warehouse.hasVehicle(vehicle)).isTrue()
        verify(exactly = 0) { shipmentStateUpdater.markAsDispatched(any()) }
    }

    // add to StandardDispatchProcessorTest
    @Test
    fun `should dispatch successfully even if the same package appears twice in the order`() {
        // Given
        val pkg = queuePackage(id = "PKG-001", weight = 300.0)
        val order = createOrder(pkg, pkg)

        // When
        val outcome = standardProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(DispatchOutcome.Dispatched(order))
        assertThat(warehouse.cargoQueue).isEmpty()
    }

    @Test
    fun `should reject with package not in queue when only one of several packages was never queued`() {
        // Given
        val queuedPackage = queuePackage(id = "PKG-001", weight = 100.0)
        val neverQueuedPackage = createPackage(id = "PKG-099", weight = 100.0)
        val order = createOrder(queuedPackage, neverQueuedPackage)

        // When
        val outcome = standardProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(
            DispatchOutcome.Rejected(order, DispatchRejectionReason.PACKAGE_NOT_IN_QUEUE)
        )
    }

    private fun queuePackage(id: String, weight: Double, priority: Priority = Priority.STANDARD): Package =
        createPackage(id, weight, priority).also { warehouse.addPackage(it) }

    private fun createPackage(id: String, weight: Double, priority: Priority = Priority.STANDARD) = Package(
        id = id,
        weight = weight,
        priority = priority,
        originWarehouse = warehouse,
        destinationWarehouse = DESTINATION
    )

    private fun createOrder(vararg packages: Package) = DispatchOrder(packages.toList(), vehicle, warehouse)

    private companion object {
        const val VEHICLE_CAPACITY_KG = 1000.0
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