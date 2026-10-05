package usecase.commands

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.IllegalShipmentTransitionException
import org.bytebloom.domain.shipment.Shipment
import org.bytebloom.domain.usecase.commands.ReroutePackageUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReroutePackageUseCaseTest {

    private lateinit var reroutePackage: ReroutePackageUseCase

    @BeforeEach
    fun setUp() {
        reroutePackage = ReroutePackageUseCase()
    }

    @Nested
    inner class AllowedBeforeDispatch {

        @Test
        fun `reroutes a freshly created shipment to the new destination`() {
            // Given
            val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
            val shipment = Shipment(createPackage())

            // When
            val rerouted = reroutePackage(shipment, newDestination)

            // Then
            assertThat(rerouted.cargo).isEqualTo(createPackage().redirectedTo(newDestination))
        }

        @Test
        fun `reroutes a shipment already assigned to a vehicle`() {
            // Given
            val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
            val shipment = Shipment(createPackage()).assignToVehicle()

            // When
            val rerouted = reroutePackage(shipment, newDestination)

            // Then
            assertThat(rerouted.cargo.destinationWarehouse).isEqualTo(newDestination)
        }

        @Test
        fun `rerouting does not change the shipment's current state`() {
            // Given
            val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
            val shipment = Shipment(createPackage()).assignToVehicle()

            // When
            val rerouted = reroutePackage(shipment, newDestination)

            // Then
            assertThat(rerouted.state).isEqualTo(shipment.state)
        }

        @Test
        fun `does not modify the original shipment`() {
            // Given
            val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
            val shipment = Shipment(createPackage())

            // When
            reroutePackage(shipment, newDestination)

            // Then
            assertThat(shipment.cargo.destinationWarehouse).isEqualTo(ORIGINAL_DESTINATION)
        }

        @Test
        fun `throws EntityValidationException when rerouted to its own origin warehouse`() {
            // Given
            val shipment = Shipment(createPackage())

            // When & Then
            assertThrows<EntityValidationException> {
                reroutePackage(shipment, ORIGIN)
            }
        }
    }

    @Nested
    inner class RejectedOnceDispatched {

        @Test
        fun `cannot reroute a shipment that is in transit`() {
            // Given
            val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
            val inTransit = Shipment(createPackage()).assignToVehicle().startTransit()

            // When
            val exception = assertThrows<IllegalShipmentTransitionException> {
                reroutePackage(inTransit, newDestination)
            }

            // Then
            assertThat(exception.currentState).isEqualTo("IN_TRANSIT")
        }

        @Test
        fun `cannot reroute a shipment that has already been delivered`() {
            // Given
            val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
            val delivered = Shipment(createPackage()).assignToVehicle().startTransit().markDelivered()

            // When & Then
            assertThrows<IllegalShipmentTransitionException> {
                reroutePackage(delivered, newDestination)
            }
        }

        @Test
        fun `cannot reroute a shipment whose delivery already failed`() {
            // Given
            val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
            val failed = Shipment(createPackage()).assignToVehicle().startTransit().markDeliveryFailed()

            // When & Then
            assertThrows<IllegalShipmentTransitionException> {
                reroutePackage(failed, newDestination)
            }
        }

        @Test
        fun `leaves the cargo untouched when rerouting is rejected`() {
            // Given
            val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
            val delivered = Shipment(createPackage()).assignToVehicle().startTransit().markDelivered()

            // When
            runCatching { reroutePackage(delivered, newDestination) }

            // Then
            assertThat(delivered.cargo.destinationWarehouse).isEqualTo(ORIGINAL_DESTINATION)
        }
    }

    private fun createPackage() = Package(
        id = "PKG-001",
        weight = 10.0,
        priority = Priority.STANDARD,
        originWarehouse = ORIGIN,
        destinationWarehouse = ORIGINAL_DESTINATION
    )

    private companion object {
        val ORIGIN = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        val ORIGINAL_DESTINATION = createWarehouse(id = "WH-002", name = "Nablus Depot")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "West Bank",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}