import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.IllegalShipmentTransitionException
import org.bytebloom.domain.model.input.ShipmentTransitionAction
import org.bytebloom.domain.shipment.Shipment
import org.bytebloom.domain.shipment.state.AssignedToVehicleState
import org.bytebloom.domain.shipment.state.CreatedState
import org.bytebloom.domain.shipment.state.DeliveredState
import org.bytebloom.domain.shipment.state.DeliveryFailedState
import org.bytebloom.domain.shipment.state.InTransitState
import org.bytebloom.domain.usecase.shipment.AdvanceShipmentStateUseCase
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame

class AdvanceShipmentStateUseCaseTest {

    private val useCase = AdvanceShipmentStateUseCase()

    private val origin = Warehouse(
        id = "WH-001",
        name = "A",
        regionalZone = "North",
        longitude = 34.0,
        latitude = 31.0
    )

    private val destination = Warehouse(
        id = "WH-002",
        name = "B",
        regionalZone = "South",
        longitude = 34.1,
        latitude = 31.1
    )

    private val cargo = Package(
        id = "PKG-001",
        weight = 10.0,
        priority = Priority.STANDARD,
        originWarehouse = origin,
        destinationWarehouse = destination
    )

    @Test
    fun `a new shipment starts in CreatedState`() {
        // Given
        val shipment = Shipment(cargo)

        // Then
        assertIs<CreatedState>(shipment.state)
    }

    @Nested
    inner class SuccessfulTransitions {

        @Test
        fun `moves from Created to AssignedToVehicle`() {
            // Given
            val shipment = Shipment(cargo)

            // When
            val result = useCase(
                shipment,
                ShipmentTransitionAction.ASSIGN_TO_VEHICLE
            )

            // Then
            assertIs<AssignedToVehicleState>(result.state)
        }

        @Test
        fun `moves from AssignedToVehicle to InTransit`() {
            // Given
            val shipment = Shipment(cargo)
                .assignToVehicle()

            // When
            val result = useCase(
                shipment,
                ShipmentTransitionAction.START_TRANSIT
            )

            // Then
            assertIs<InTransitState>(result.state)
        }

        @Test
        fun `moves from InTransit to Delivered`() {
            // Given
            val shipment = Shipment(cargo)
                .assignToVehicle()
                .startTransit()

            // When
            val result = useCase(
                shipment,
                ShipmentTransitionAction.MARK_DELIVERED
            )

            // Then
            assertIs<DeliveredState>(result.state)
        }

        @Test
        fun `moves from InTransit to DeliveryFailed`() {
            // Given
            val shipment = Shipment(cargo)
                .assignToVehicle()
                .startTransit()

            // When
            val result = useCase(
                shipment,
                ShipmentTransitionAction.MARK_DELIVERY_FAILED
            )

            // Then
            assertIs<DeliveryFailedState>(result.state)
        }

        @Test
        fun `preserves the same cargo reference across transitions`() {
            // Given
            val shipment = Shipment(cargo)

            // When
            val result = shipment
                .assignToVehicle()
                .startTransit()
                .markDelivered()

            // Then
            assertSame(cargo, result.cargo)
        }
    }

    @Nested
    inner class IllegalTransitionsFromCreated {

        private val shipment = Shipment(cargo)

        @Test
        fun `cannot start transit before vehicle assignment`() {
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                useCase(
                    shipment,
                    ShipmentTransitionAction.START_TRANSIT
                )
            }

            assertEquals("CREATED", exception.currentState)
            assertEquals("start transit", exception.attemptedAction)
        }

        @Test
        fun `cannot mark shipment as delivered directly`() {
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                useCase(
                    shipment,
                    ShipmentTransitionAction.MARK_DELIVERED
                )
            }

            assertEquals("CREATED", exception.currentState)
        }

        @Test
        fun `cannot mark shipment as failed directly`() {
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                useCase(
                    shipment,
                    ShipmentTransitionAction.MARK_DELIVERY_FAILED
                )
            }

            assertEquals("CREATED", exception.currentState)
        }
    }

    @Nested
    inner class IllegalTransitionsFromAssignedToVehicle {

        private val shipment = Shipment(cargo)
            .assignToVehicle()

        @Test
        fun `cannot assign shipment to another vehicle`() {
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                useCase(
                    shipment,
                    ShipmentTransitionAction.ASSIGN_TO_VEHICLE
                )
            }

            assertEquals(
                "ASSIGNED_TO_VEHICLE",
                exception.currentState
            )
        }

        @Test
        fun `cannot mark shipment as delivered before transit`() {
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                useCase(
                    shipment,
                    ShipmentTransitionAction.MARK_DELIVERED
                )
            }

            assertEquals(
                "ASSIGNED_TO_VEHICLE",
                exception.currentState
            )
        }

        @Test
        fun `cannot mark shipment as failed before transit`() {
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                useCase(
                    shipment,
                    ShipmentTransitionAction.MARK_DELIVERY_FAILED
                )
            }

            assertEquals(
                "ASSIGNED_TO_VEHICLE",
                exception.currentState
            )
        }
    }

    @Nested
    inner class IllegalTransitionsFromInTransit {

        private val shipment = Shipment(cargo)
            .assignToVehicle()
            .startTransit()

        @Test
        fun `cannot assign shipment to another vehicle`() {
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                useCase(
                    shipment,
                    ShipmentTransitionAction.ASSIGN_TO_VEHICLE
                )
            }

            assertEquals("IN_TRANSIT", exception.currentState)
        }

        @Test
        fun `cannot start transit twice`() {
            val exception = assertFailsWith<IllegalShipmentTransitionException> {
                useCase(
                    shipment,
                    ShipmentTransitionAction.START_TRANSIT
                )
            }

            assertEquals("IN_TRANSIT", exception.currentState)
        }
    }

    @Nested
    inner class TerminalStates {

        @Test
        fun `delivered shipment cannot transition again`() {
            val shipment = Shipment(cargo)
                .assignToVehicle()
                .startTransit()
                .markDelivered()

            assertFailsWith<IllegalShipmentTransitionException> {
                shipment.assignToVehicle()
            }

            assertFailsWith<IllegalShipmentTransitionException> {
                shipment.startTransit()
            }

            assertFailsWith<IllegalShipmentTransitionException> {
                shipment.markDelivered()
            }

            assertFailsWith<IllegalShipmentTransitionException> {
                shipment.markDeliveryFailed()
            }
        }

        @Test
        fun `failed shipment cannot transition again`() {
            val shipment = Shipment(cargo)
                .assignToVehicle()
                .startTransit()
                .markDeliveryFailed()

            assertFailsWith<IllegalShipmentTransitionException> {
                shipment.assignToVehicle()
            }

            assertFailsWith<IllegalShipmentTransitionException> {
                shipment.startTransit()
            }

            assertFailsWith<IllegalShipmentTransitionException> {
                shipment.markDelivered()
            }

            assertFailsWith<IllegalShipmentTransitionException> {
                shipment.markDeliveryFailed()
            }
        }
    }
}