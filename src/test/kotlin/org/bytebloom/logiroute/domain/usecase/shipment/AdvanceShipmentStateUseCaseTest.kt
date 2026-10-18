package org.bytebloom.logiroute.domain.usecase.shipment

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.IllegalShipmentTransitionException
import org.bytebloom.logiroute.domain.model.input.ShipmentTransitionAction
import org.bytebloom.logiroute.domain.shipment.Shipment
import org.bytebloom.logiroute.domain.shipment.state.AssignedToVehicleState
import org.bytebloom.logiroute.domain.shipment.state.CreatedState
import org.bytebloom.logiroute.domain.shipment.state.DeliveredState
import org.bytebloom.logiroute.domain.shipment.state.DeliveryFailedState
import org.bytebloom.logiroute.domain.shipment.state.InTransitState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertFailsWith

class AdvanceShipmentStateUseCaseTest {

    private lateinit var advanceShipmentState: AdvanceShipmentStateUseCase

    @BeforeEach
    fun setUp() {
        advanceShipmentState = AdvanceShipmentStateUseCase()
    }

    @Test
    fun `should start in created state when shipment is new`() {
        // Given
        val shipment = createdShipment()

        // When
        val state = shipment.state

        // Then
        assertThat(state).isInstanceOf(CreatedState::class.java)
    }

    @Nested
    inner class SuccessfulTransitions {

        @Test
        fun `should move to assigned state when vehicle is assigned to created shipment`() {
            // Given
            val shipment = createdShipment()

            // When
            val result = advanceShipmentState(shipment, ShipmentTransitionAction.ASSIGN_TO_VEHICLE)

            // Then
            assertThat(result.state).isInstanceOf(AssignedToVehicleState::class.java)
        }

        @Test
        fun `should move to in transit state when assigned shipment starts transit`() {
            // Given
            val shipment = assignedShipment()

            // When
            val result = advanceShipmentState(shipment, ShipmentTransitionAction.START_TRANSIT)

            // Then
            assertThat(result.state).isInstanceOf(InTransitState::class.java)
        }

        @Test
        fun `should move to delivered state when in transit shipment is delivered`() {
            // Given
            val shipment = inTransitShipment()

            // When
            val result = advanceShipmentState(shipment, ShipmentTransitionAction.MARK_DELIVERED)

            // Then
            assertThat(result.state).isInstanceOf(DeliveredState::class.java)
        }

        @Test
        fun `should move to delivery failed state when in transit shipment fails`() {
            // Given
            val shipment = inTransitShipment()

            // When
            val result = advanceShipmentState(shipment, ShipmentTransitionAction.MARK_DELIVERY_FAILED)

            // Then
            assertThat(result.state).isInstanceOf(DeliveryFailedState::class.java)
        }

        @Test
        fun `should keep same cargo when shipment moves through full lifecycle`() {
            // Given
            val shipment = createdShipment()

            // When
            val delivered = listOf(
                ShipmentTransitionAction.ASSIGN_TO_VEHICLE,
                ShipmentTransitionAction.START_TRANSIT,
                ShipmentTransitionAction.MARK_DELIVERED
            ).fold(shipment) { current, action -> advanceShipmentState(current, action) }

            // Then
            assertThat(delivered.cargo).isSameInstanceAs(CARGO)
        }
    }

    @Nested
    inner class IllegalTransitionsFromCreated {

        @ParameterizedTest
        @EnumSource(names = ["START_TRANSIT", "MARK_DELIVERED", "MARK_DELIVERY_FAILED"])
        fun `should reject action when shipment is not yet assigned`(action: ShipmentTransitionAction) {
            // Given
            val shipment = createdShipment()

            // When
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                advanceShipmentState(shipment, action)
            }

            // Then
            assertThat(exception.currentState).isEqualTo("CREATED")
        }

        @Test
        fun `should describe attempted action when transit starts before assignment`() {
            // Given
            val shipment = createdShipment()

            // When
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                advanceShipmentState(shipment, ShipmentTransitionAction.START_TRANSIT)
            }

            // Then
            assertThat(exception.attemptedAction).isEqualTo("start transit")
        }
    }

    @Nested
    inner class IllegalTransitionsFromAssignedToVehicle {

        @ParameterizedTest
        @EnumSource(names = ["ASSIGN_TO_VEHICLE", "MARK_DELIVERED", "MARK_DELIVERY_FAILED"])
        fun `should reject action when shipment is assigned but not in transit`(action: ShipmentTransitionAction) {
            // Given
            val shipment = assignedShipment()

            // When
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                advanceShipmentState(shipment, action)
            }

            // Then
            assertThat(exception.currentState).isEqualTo("ASSIGNED_TO_VEHICLE")
        }
    }

    @Nested
    inner class IllegalTransitionsFromInTransit {

        @ParameterizedTest
        @EnumSource(names = ["ASSIGN_TO_VEHICLE", "START_TRANSIT"])
        fun `should reject action when shipment is already in transit`(action: ShipmentTransitionAction) {
            // Given
            val shipment = inTransitShipment()

            // When
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                advanceShipmentState(shipment, action)
            }

            // Then
            assertThat(exception.currentState).isEqualTo("IN_TRANSIT")
        }
    }

    @Nested
    inner class TerminalStates {

        @ParameterizedTest
        @EnumSource(ShipmentTransitionAction::class)
        fun `should reject any action when shipment is delivered`(action: ShipmentTransitionAction) {
            // Given
            val shipment = deliveredShipment()

            // When / Then
            assertFailsWith<IllegalShipmentTransitionException> { advanceShipmentState(shipment, action) }
        }

        @ParameterizedTest
        @EnumSource(ShipmentTransitionAction::class)
        fun `should reject any action when shipment delivery failed`(action: ShipmentTransitionAction) {
            // Given
            val shipment = failedShipment()

            // When / Then
            assertFailsWith<IllegalShipmentTransitionException> { advanceShipmentState(shipment, action) }
        }
    }

    private fun createdShipment() = Shipment(CARGO)

    private fun assignedShipment() = createdShipment().assignToVehicle()

    private fun inTransitShipment() = assignedShipment().startTransit()

    private fun deliveredShipment() = inTransitShipment().markDelivered()

    private fun failedShipment() = inTransitShipment().markDeliveryFailed()

    private companion object {
        val CARGO = Package(
            id = "PKG-001",
            weight = 10.0,
            priority = Priority.STANDARD,
            originWarehouse = createWarehouse("WH-001"),
            destinationWarehouse = createWarehouse("WH-002")
        )

        fun createWarehouse(id: String) = Warehouse(
            id = id,
            name = "Hub $id",
            regionalZone = "CENTRAL",
            longitude = 34.0,
            latitude = 31.0
        )
    }
}