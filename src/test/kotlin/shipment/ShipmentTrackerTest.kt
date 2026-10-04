package shipment

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.dispatch.DispatchOrder
import org.bytebloom.domain.dispatch.DispatchOutcome
import org.bytebloom.domain.dispatch.StandardDispatchProcessor
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.IllegalShipmentTransitionException
import org.bytebloom.domain.shipment.ShipmentTracker
import org.bytebloom.domain.shipment.state.CreatedState
import org.bytebloom.domain.shipment.state.InTransitState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShipmentTrackerTest {

    private lateinit var shipmentTracker: ShipmentTracker
    private lateinit var warehouse: Warehouse
    private lateinit var vehicle: Vehicle

    @BeforeEach
    fun setUp() {
        shipmentTracker = ShipmentTracker()
        warehouse = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        vehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = VEHICLE_CAPACITY_KG,
            costPerKm = 2.5,
            currentWarehouse = warehouse
        )
        warehouse.addVehicle(vehicle)
    }

    @Nested
    inner class Tracking {

        @Test
        fun `should start a newly tracked package in created state`() {
            // Given
            val pkg = createPackage(id = "PKG-001")

            // When
            shipmentTracker.track(pkg)

            // Then
            assertThat(shipmentTracker.stateOf(pkg.id)).isInstanceOf(CreatedState::class.java)
        }

        @Test
        fun `should return the same shipment when a package is tracked twice`() {
            // Given
            val pkg = createPackage(id = "PKG-001")
            val firstShipment = shipmentTracker.track(pkg)

            // When
            val secondShipment = shipmentTracker.track(pkg)

            // Then
            assertThat(secondShipment).isSameInstanceAs(firstShipment)
            assertThat(shipmentTracker.historyOf(pkg.id)).hasSize(1)
        }

        @Test
        fun `should return no state and empty history for an untracked package`() {
            // Given
            val untrackedPackageId = "PKG-404"

            // When
            val state = shipmentTracker.stateOf(untrackedPackageId)
            val history = shipmentTracker.historyOf(untrackedPackageId)

            // Then
            assertThat(state).isNull()
            assertThat(history).isEmpty()
        }
    }

    @Nested
    inner class MarkingAsDispatched {

        @Test
        fun `should move dispatched package to in transit state`() {
            // Given
            val pkg = createPackage(id = "PKG-001")
            shipmentTracker.track(pkg)

            // When
            shipmentTracker.markAsDispatched(createOrder(pkg))

            // Then
            assertThat(shipmentTracker.stateOf(pkg.id)).isInstanceOf(InTransitState::class.java)
        }

        @Test
        fun `should record every state transition in order`() {
            // Given
            val pkg = createPackage(id = "PKG-001")
            shipmentTracker.track(pkg)

            // When
            shipmentTracker.markAsDispatched(createOrder(pkg))

            // Then
            assertThat(stateNamesOf(pkg)).containsExactly("CREATED", "ASSIGNED_TO_VEHICLE", "IN_TRANSIT").inOrder()
        }

        @Test
        fun `should start from created state when dispatched package was not tracked before`() {
            // Given
            val untrackedPackage = createPackage(id = "PKG-001")

            // When
            shipmentTracker.markAsDispatched(createOrder(untrackedPackage))

            // Then
            assertThat(stateNamesOf(untrackedPackage))
                .containsExactly("CREATED", "ASSIGNED_TO_VEHICLE", "IN_TRANSIT").inOrder()
        }

        @Test
        fun `should not change packages that are not part of the dispatched order`() {
            // Given
            val dispatchedPackage = createPackage(id = "PKG-001")
            val waitingPackage = createPackage(id = "PKG-002")
            shipmentTracker.track(waitingPackage)

            // When
            shipmentTracker.markAsDispatched(createOrder(dispatchedPackage))

            // Then
            assertThat(shipmentTracker.stateOf(waitingPackage.id)).isInstanceOf(CreatedState::class.java)
        }

        @Test
        fun `should reject dispatching a package that is already in transit`() {
            // Given
            val pkg = createPackage(id = "PKG-001")
            shipmentTracker.markAsDispatched(createOrder(pkg))

            // When & Then
            assertFailsWith<IllegalShipmentTransitionException> {
                shipmentTracker.markAsDispatched(createOrder(pkg))
            }
            assertThat(shipmentTracker.stateOf(pkg.id)).isInstanceOf(InTransitState::class.java)
        }
    }

    @Nested
    inner class IntegrationWithDispatchTemplate {

        @Test
        fun `should move all packages to in transit when standard processor dispatches them`() {
            // Given
            val standardProcessor = StandardDispatchProcessor(shipmentTracker)
            val packages = listOf(queuePackage(id = "PKG-001"), queuePackage(id = "PKG-002"))
            packages.forEach(shipmentTracker::track)

            // When
            val outcome = standardProcessor.dispatch(createOrder(*packages.toTypedArray()))

            // Then
            assertThat(outcome).isInstanceOf(DispatchOutcome.Dispatched::class.java)
            packages.forEach { pkg ->
                assertThat(shipmentTracker.stateOf(pkg.id)).isInstanceOf(InTransitState::class.java)
            }
        }

        @Test
        fun `should keep packages in created state when standard processor rejects the order`() {
            // Given
            val standardProcessor = StandardDispatchProcessor(shipmentTracker)
            val heavyPackage = queuePackage(id = "PKG-001", weight = VEHICLE_CAPACITY_KG + 1)
            shipmentTracker.track(heavyPackage)

            // When
            val outcome = standardProcessor.dispatch(createOrder(heavyPackage))

            // Then
            assertThat(outcome).isInstanceOf(DispatchOutcome.Rejected::class.java)
            assertThat(shipmentTracker.stateOf(heavyPackage.id)).isInstanceOf(CreatedState::class.java)
        }
    }

    private fun stateNamesOf(pkg: Package): List<String> =
        shipmentTracker.historyOf(pkg.id).map { it.name }

    private fun queuePackage(id: String, weight: Double = 100.0): Package =
        createPackage(id, weight).also { warehouse.addPackage(it) }

    private fun createPackage(id: String, weight: Double = 100.0) = Package(
        id = id,
        weight = weight,
        priority = Priority.STANDARD,
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