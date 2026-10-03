package usecase.commands

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bytebloom.domain.dispatch.DispatchMode
import org.bytebloom.domain.dispatch.DispatchOrder
import org.bytebloom.domain.dispatch.DispatchOutcome
import org.bytebloom.domain.dispatch.DispatchRejectionReason
import org.bytebloom.domain.dispatch.ExpressDispatchProcessor
import org.bytebloom.domain.dispatch.StandardDispatchProcessor
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.DispatchCargoUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DispatchCargoUseCaseTest {

    private lateinit var standardProcessor: StandardDispatchProcessor
    private lateinit var expressProcessor: ExpressDispatchProcessor
    private lateinit var dispatchCargo: DispatchCargoUseCase
    private lateinit var order: DispatchOrder

    @BeforeEach
    fun setUp() {
        standardProcessor = mockk()
        expressProcessor = mockk()
        dispatchCargo = DispatchCargoUseCase(standardProcessor, expressProcessor)
        order = createOrder()
    }

    @Test
    fun `should dispatch through standard processor when mode is standard`() {
        // Given
        val expectedOutcome = DispatchOutcome.Dispatched(order)
        every { standardProcessor.dispatch(order) } returns expectedOutcome

        // When
        val outcome = dispatchCargo(order, DispatchMode.STANDARD)

        // Then
        assertThat(outcome).isEqualTo(expectedOutcome)
        verify(exactly = 1) { standardProcessor.dispatch(order) }
    }

    @Test
    fun `should dispatch through express processor when mode is express`() {
        // Given
        val expectedOutcome = DispatchOutcome.Dispatched(order)
        every { expressProcessor.dispatch(order) } returns expectedOutcome

        // When
        val outcome = dispatchCargo(order, DispatchMode.EXPRESS)

        // Then
        assertThat(outcome).isEqualTo(expectedOutcome)
        verify(exactly = 1) { expressProcessor.dispatch(order) }
    }

    @Test
    fun `should not use express processor when mode is standard`() {
        // Given
        every { standardProcessor.dispatch(order) } returns DispatchOutcome.Dispatched(order)

        // When
        dispatchCargo(order, DispatchMode.STANDARD)

        // Then
        verify(exactly = 0) { expressProcessor.dispatch(any()) }
    }

    @Test
    fun `should not use standard processor when mode is express`() {
        // Given
        every { expressProcessor.dispatch(order) } returns DispatchOutcome.Dispatched(order)

        // When
        dispatchCargo(order, DispatchMode.EXPRESS)

        // Then
        verify(exactly = 0) { standardProcessor.dispatch(any()) }
    }

    @Test
    fun `should return rejection from processor without changing it`() {
        // Given
        val rejection = DispatchOutcome.Rejected(order, DispatchRejectionReason.PRIORITY_NOT_ALLOWED)
        every { expressProcessor.dispatch(order) } returns rejection

        // When
        val outcome = dispatchCargo(order, DispatchMode.EXPRESS)

        // Then
        assertThat(outcome).isEqualTo(rejection)
    }

    private fun createOrder(): DispatchOrder {
        val origin = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        val destination = createWarehouse(id = "WH-002", name = "Nablus Depot")
        val pkg = Package(
            id = "PKG-001",
            weight = 100.0,
            priority = Priority.URGENT,
            originWarehouse = origin,
            destinationWarehouse = destination
        )
        val vehicle = Vehicle(id = "TRK-001", maxCapacityKg = 1000.0, costPerKm = 2.5, currentWarehouse = origin)
        return DispatchOrder(packages = listOf(pkg), vehicle = vehicle, warehouse = origin)
    }

    private fun createWarehouse(id: String, name: String) = Warehouse(
        id = id,
        name = name,
        regionalZone = "West Bank",
        longitude = 35.2,
        latitude = 31.9
    )
}