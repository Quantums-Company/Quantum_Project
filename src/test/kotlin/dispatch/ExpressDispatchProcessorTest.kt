package dispatch

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.mockk.verify
import org.bytebloom.domain.dispatch.DispatchNotifier
import org.bytebloom.domain.dispatch.DispatchOrder
import org.bytebloom.domain.dispatch.DispatchOutcome
import org.bytebloom.domain.dispatch.DispatchRejectionReason
import org.bytebloom.domain.dispatch.ExpressDispatchProcessor
import org.bytebloom.domain.dispatch.ShipmentStateUpdater
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExpressDispatchProcessorTest {

    private lateinit var shipmentStateUpdater: ShipmentStateUpdater
    private lateinit var dispatchNotifier: DispatchNotifier
    private lateinit var expressProcessor: ExpressDispatchProcessor
    private lateinit var warehouse: Warehouse
    private lateinit var vehicle: Vehicle

    @BeforeEach
    fun setUp() {
        shipmentStateUpdater = mockk(relaxUnitFun = true)
        dispatchNotifier = mockk(relaxUnitFun = true)
        expressProcessor = ExpressDispatchProcessor(shipmentStateUpdater, dispatchNotifier)
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
    fun `should dispatch urgent packages within express load limit`() {
        // Given
        val order = createOrder(
            queueUrgentPackage(id = "PKG-001", weight = 300.0),
            queueUrgentPackage(id = "PKG-002", weight = 400.0)
        )

        // When
        val outcome = expressProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(DispatchOutcome.Dispatched(order))
    }

    @Test
    fun `should dispatch when total weight exactly equals eighty percent of capacity`() {
        // Given
        val order = createOrder(queueUrgentPackage(id = "PKG-001", weight = 800.0))

        // When
        val outcome = expressProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(DispatchOutcome.Dispatched(order))
    }

    @Test
    fun `should reject with capacity exceeded when weight is above express limit but below full capacity`() {
        // Given
        val order = createOrder(queueUrgentPackage(id = "PKG-001", weight = 850.0))

        // When
        val outcome = expressProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(
            DispatchOutcome.Rejected(order, DispatchRejectionReason.CAPACITY_EXCEEDED)
        )
    }

    @Test
    fun `should reject with priority not allowed when any package is not urgent`() {
        // Given
        val order = createOrder(
            queueUrgentPackage(id = "PKG-001", weight = 100.0),
            queuePackage(id = "PKG-002", weight = 100.0, priority = Priority.STANDARD)
        )

        // When
        val outcome = expressProcessor.dispatch(order)

        // Then
        assertThat(outcome).isEqualTo(
            DispatchOutcome.Rejected(order, DispatchRejectionReason.PRIORITY_NOT_ALLOWED)
        )
    }

    @Test
    fun `should notify dispatch status with dispatched outcome when dispatch succeeds`() {
        // Given
        val order = createOrder(queueUrgentPackage(id = "PKG-001", weight = 300.0))

        // When
        expressProcessor.dispatch(order)

        // Then
        verify(exactly = 1) { dispatchNotifier.notify(DispatchOutcome.Dispatched(order)) }
    }

    @Test
    fun `should notify dispatch status with rejection reason when dispatch is rejected`() {
        // Given
        val order = createOrder(queuePackage(id = "PKG-001", weight = 100.0, priority = Priority.LOW))

        // When
        expressProcessor.dispatch(order)

        // Then
        verify(exactly = 1) {
            dispatchNotifier.notify(DispatchOutcome.Rejected(order, DispatchRejectionReason.PRIORITY_NOT_ALLOWED))
        }
    }

    @Test
    fun `should update shipment state only when dispatch succeeds`() {
        // Given
        val acceptedOrder = createOrder(queueUrgentPackage(id = "PKG-001", weight = 300.0))

        // When
        expressProcessor.dispatch(acceptedOrder)

        // Then
        verify(exactly = 1) { shipmentStateUpdater.markAsDispatched(acceptedOrder) }
    }

    @Test
    fun `should skip shipment state update when dispatch is rejected`() {
        // Given
        val rejectedOrder = createOrder(queueUrgentPackage(id = "PKG-001", weight = 900.0))

        // When
        expressProcessor.dispatch(rejectedOrder)

        // Then
        verify(exactly = 0) { shipmentStateUpdater.markAsDispatched(any()) }
    }

    private fun queueUrgentPackage(id: String, weight: Double): Package =
        queuePackage(id, weight, Priority.URGENT)

    private fun queuePackage(id: String, weight: Double, priority: Priority): Package = Package(
        id = id,
        weight = weight,
        priority = priority,
        originWarehouse = warehouse,
        destinationWarehouse = DESTINATION
    ).also { warehouse.addPackage(it) }

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