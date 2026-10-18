package org.bytebloom.logiroute.domain.usecase.dispatch

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.IllegalShipmentTransitionException
import org.bytebloom.logiroute.domain.shipment.Shipment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ReroutePackageUseCaseTest {

    private lateinit var reroutePackage: ReroutePackageUseCase

    @BeforeEach
    fun setUp() {
        reroutePackage = ReroutePackageUseCase()
    }

    @Nested
    inner class AllowedBeforeDispatch {

        @Test
        fun `should redirect cargo to new destination when shipment is created`() {
            // Given
            val shipment = Shipment(createPackage())

            // When
            val rerouted = reroutePackage(shipment, NEW_DESTINATION)

            // Then
            assertThat(rerouted.cargo).isEqualTo(createPackage().redirectedTo(NEW_DESTINATION))
        }

        @Test
        fun `should redirect cargo to new destination when shipment is assigned to vehicle`() {
            // Given
            val shipment = Shipment(createPackage()).assignToVehicle()

            // When
            val rerouted = reroutePackage(shipment, NEW_DESTINATION)

            // Then
            assertThat(rerouted.cargo.destinationWarehouse).isEqualTo(NEW_DESTINATION)
        }

        @Test
        fun `should keep shipment state unchanged when rerouted`() {
            // Given
            val shipment = Shipment(createPackage()).assignToVehicle()

            // When
            val rerouted = reroutePackage(shipment, NEW_DESTINATION)

            // Then
            assertThat(rerouted.state).isEqualTo(shipment.state)
        }

        @Test
        fun `should leave original shipment untouched when rerouted`() {
            // Given
            val shipment = Shipment(createPackage())

            // When
            reroutePackage(shipment, NEW_DESTINATION)

            // Then
            assertThat(shipment.cargo.destinationWarehouse).isEqualTo(ORIGINAL_DESTINATION)
        }

        @Test
        fun `should throw validation exception when new destination is the origin`() {
            // Given
            val shipment = Shipment(createPackage())

            // When / Then
            assertFailsWith<EntityValidationException> { reroutePackage(shipment, ORIGIN) }
        }
    }

    @Nested
    inner class RejectedOnceDispatched {

        @Test
        fun `should throw transition exception when shipment is in transit`() {
            // Given
            val inTransit = Shipment(createPackage()).assignToVehicle().startTransit()

            // When
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                reroutePackage(inTransit, NEW_DESTINATION)
            }

            // Then
            assertThat(exception.currentState).isEqualTo("IN_TRANSIT")
        }

        @Test
        fun `should throw transition exception when shipment is delivered`() {
            // Given
            val delivered = Shipment(createPackage()).assignToVehicle().startTransit().markDelivered()

            // When / Then
            assertFailsWith<IllegalShipmentTransitionException> { reroutePackage(delivered, NEW_DESTINATION) }
        }

        @Test
        fun `should throw transition exception when shipment delivery failed`() {
            // Given
            val failed = Shipment(createPackage()).assignToVehicle().startTransit().markDeliveryFailed()

            // When / Then
            assertFailsWith<IllegalShipmentTransitionException> { reroutePackage(failed, NEW_DESTINATION) }
        }

        @Test
        fun `should leave cargo untouched when rerouting is rejected`() {
            // Given
            val delivered = Shipment(createPackage()).assignToVehicle().startTransit().markDelivered()

            // When
            runCatching { reroutePackage(delivered, NEW_DESTINATION) }

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
        val NEW_DESTINATION = createWarehouse(id = "WH-003", name = "Hebron Depot")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "CENTRAL",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}